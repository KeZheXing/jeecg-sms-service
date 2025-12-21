package org.jeecg.modules.airag.app.utils;

import org.jeecg.modules.airag.app.entity.SmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastTemplateMatcher {

    private final List<SmsTemplate> templates;
    private final int similarityThreshold; // 相似度阈值（0-100，越高越严格）

    // 构造器：初始化模板和相似度阈值
    public FastTemplateMatcher(List<SmsTemplate> templates, int similarityThreshold) {
        this.templates = templates;
        this.similarityThreshold = similarityThreshold;
    }

    /**
     * 提取验证码：先匹配模板，再提取内容
     */
    public String extractCaptcha(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.err.println("输入不能为空");
            return null;
        }
        String inputTrimmed = input.trim();

        // 1. 快速匹配最相似的模板
        SmsTemplate matchedTemplate = findMostSimilarTemplate(inputTrimmed);
        if (matchedTemplate == null) {
            System.err.println("未找到匹配的模板");
            return null;
        }
        System.out.println("匹配到模板：" + matchedTemplate.getId());

        // 2. 从匹配的模板中定位验证码位置并提取
        return extractFromTemplate(inputTrimmed, matchedTemplate);
    }

    /**
     * 找到与输入最相似的模板（基于编辑距离计算相似度）
     */
    private SmsTemplate findMostSimilarTemplate(String input) {
        SmsTemplate bestMatch = null;
        int maxSimilarity = 0;

        for (SmsTemplate template : templates) {
            String templateText = template.getTemplateContent().trim();
            // 计算输入与模板的相似度（0-100）
            String inputFixedCandidate = getInputFixedCandidate(input, template);
            int similarity = calculateSimilarity(inputFixedCandidate, templateText.replace(template.getPlaceholder(),""));
            // 保留相似度最高且超过阈值的模板
            if (similarity > maxSimilarity && similarity >= similarityThreshold) {
                maxSimilarity = similarity;
                bestMatch = template;
            }
        }
        return bestMatch;
    }

    /**
     * 生成输入的固定文本候选：假设输入中存在与模板验证码长度匹配的部分，剔除后作为候选
     */
    private String getInputFixedCandidate(String input, SmsTemplate template) {
        int captchaLen = template.getCaptchaLength();
        Pattern captchaPattern = template.getCaptchaPattern();

        // 尝试找到输入中符合验证码特征的子串，剔除后作为固定文本候选
        Matcher matcher = captchaPattern.matcher(input);
        while (matcher.find()) {
            String candidateCaptcha = matcher.group();
            if (candidateCaptcha.length() == captchaLen) {
                // 剔除该候选验证码，得到固定文本候选
                return input.replace(candidateCaptcha, "").trim();
            }
        }

        // 若未找到符合特征的验证码，直接返回输入（作为降级方案）
        return input.trim();
    }

    /**
     * 计算两个字符串的相似度（基于Levenshtein编辑距离）
     * 相似度 = (1 - 编辑距离 / 最长字符串长度) * 100
     */
    private int calculateSimilarity(String s1, String s2) {
        if (s1.isEmpty() || s2.isEmpty()) return 0;
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        // 初始化编辑距离矩阵
        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        // 计算编辑距离
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        // 计算相似度（0-100）
        int maxLen = Math.max(len1, len2);
        return maxLen == 0 ? 100 : (int) ((1 - (double) dp[len1][len2] / maxLen) * 100);
    }

    /**
     * 从匹配的模板中提取验证码
     */
    private String extractFromTemplate(String input, SmsTemplate template) {
        String templateText = template.getTemplateContent().trim();
        String placeholder = template.getPlaceholder();
        int placeholderLen = placeholder.length();

        // 1. 找到模板中占位符的位置
        int placeholderStart = templateText.indexOf(placeholder);
        if (placeholderStart == -1) {
            System.err.println("模板[" + template.getId() + "]中未找到占位符");
            return null;
        }
        int placeholderEnd = placeholderStart + placeholderLen;

        // 2. 模板中占位符前后的固定文本（用于定位输入中的验证码位置）
        String templatePrefix = templateText.substring(0, placeholderStart);
        String templateSuffix = templateText.substring(placeholderEnd);

        // 3. 在输入中找到与模板前缀、后缀匹配的位置，确定验证码范围
        int inputPrefixEnd = input.indexOf(templatePrefix) + templatePrefix.length();
        int inputSuffixStart = input.indexOf(templateSuffix, inputPrefixEnd);

        // 校验前缀后缀在输入中的位置
        if (inputPrefixEnd < templatePrefix.length() || inputSuffixStart == -1) {
            System.err.println("模板[" + template.getId() + "]与输入的固定部分不匹配");
            return null;
        }

        // 4. 提取验证码（输入中前缀结束到后缀开始之间的内容）
        String captcha = input.substring(inputPrefixEnd, inputSuffixStart).trim();

        // 5. 校验验证码长度和格式
        if (captcha.length() != template.getCaptchaLength()) {
            System.err.println("验证码长度不符（预期" + template.getCaptchaLength() + "位，实际" + captcha.length() + "位）");
            return null;
        }
        Matcher matcher = template.getCaptchaPattern().matcher(captcha);
        if (!matcher.matches()) {
            System.err.println("验证码格式不符（预期" + template.getCaptchaPattern().pattern() + "）");
            return null;
        }

        return template.getRentId()+"----"+captcha;
    }

//
//
//    // 测试
//    public static void main(String[] args) {
//        // 定义多个模板
//        List<SmsTemplate> templates = new ArrayList<>();
//        // 模板1：短信验证码
//        templates.add(new SmsTemplate(
//                "您的WhatsApp Business 驗證碼:xxxxxx 請不要與他人分享這個密碼",
//                "xxxxxx",
//                6,
//                "\\d+" // 6位数字
//        ));
//        // 模板2：邮箱验证码
//        templates.add(new SmsTemplate(
//                "邮箱验证码为: xxxxxx，请在5分钟内使用",
//                "xxxxxx",
//                6,
//                "\\d+" // 6位数字
//        ));
//        // 模板3：APP验证码（带字母）
//        templates.add(new SmsTemplate(
//                "APP安全验证：xxxxxx（5分钟有效）",
//                "xxxxxx",
//                6,
//                "[A-Za-z0-9]+" // 6位字母数字混合
//        ));
//
//        // 初始化提取器（相似度阈值设为80，可根据需求调整）
//        FastTemplateMatcher extractor = new FastTemplateMatcher(templates, 80);
//
//        // 测试用例
//        testExtract(extractor, "您的WhatsApp Business 驗證碼:750-816 請不要與他人分享這個密碼".replace("-","")); // 匹配sms，提取123456
//        testExtract(extractor, "邮箱验证码为: 654321，请在5分钟内使用"); // 匹配email，提取654321
//        testExtract(extractor, "APP安全验证：aBc123（5分钟有效）"); // 匹配app，提取aBc123
//        testExtract(extractor, "无效的字符串"); // 无匹配模板
//    }

    public static String testExtract(FastTemplateMatcher extractor, String input) {
        System.out.println("输入：" + input);
        String captcha = extractor.extractCaptcha(input);
        System.out.println("提取结果：" + (captcha != null ? captcha : "失败") + "\n");
        return captcha;
    }
}
