package org.jeecg.modules.sms.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.common.util.encryption.AesEncryptUtil;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsPrice;
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.airag.app.entity.SmsTemplate;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.sms.entity.bo.SmsCodeMatchBO;
import org.jeecg.modules.sms.mapper.SmsPriceMapper;
import org.jeecg.modules.sms.mapper.SmsRentMapper;
import org.jeecg.modules.sms.mapper.SmsTemplateMapper;
import org.jeecg.modules.sms.service.ISmsRentService;
import org.jeecg.modules.sms.utils.SnowFlakeIdGenerator;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
@Slf4j
public class SmsRentServiceImpl extends ServiceImpl<SmsRentMapper, SmsRent> implements ISmsRentService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SmsTemplateMapper smsTemplateMapper;
    @Autowired
    private SmsDeviceMapper smsDeviceMapper;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SmsPriceMapper smsPriceMapper;
    @Autowired
    private CommonAPI commonAPI;
    @Autowired
    private TelegramBot.MyTelegramBot telegramBot;

    @Override
    public Result<IPage<SmsRent>> queryPageList(HttpServletRequest req, QueryWrapper<SmsRent> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsRent>> result = new Result<IPage<SmsRent>>();
        //TODO 外部模拟登陆临时账号，列表不显示
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Set<String> roles = commonAPI.queryUserRoles(username);
        if (!(roles != null && roles.contains("admin"))) {
            queryWrapper.eq("user_name", username);
        }
        Page<SmsRent> page = new Page<SmsRent>(pageNo, pageSize);
        IPage<SmsRent> pageList = this.page(page, queryWrapper);
        List<SmsRent> records = pageList.getRecords();
        if (records.size() > 0) {
            Set<String> collect = records.stream().map(SmsRent::getProjectCode).collect(Collectors.toSet());
            LambdaQueryWrapper<SmsTemplate> templateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            templateLambdaQueryWrapper.in(SmsTemplate::getTemplateCode, collect);
            Map<String, String> map = smsTemplateMapper.selectList(templateLambdaQueryWrapper).stream().collect(Collectors.toMap(SmsTemplate::getTemplateCode, SmsTemplate::getTemplateName));
            records.forEach(e -> {
                e.setProjectName(map.get(e.getProjectCode()) + "(" + e.getProjectCode() + ")");
            });
        }
        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

    @Override
    public Result<IPage<SmsRent>> queryBlackAllPageList(HttpServletRequest req, QueryWrapper<SmsRent> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsRent>> result = new Result<IPage<SmsRent>>();
        //TODO 外部模拟登陆临时账号，列表不显示
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Set<String> roles = commonAPI.queryUserRoles(username);
        if (!(roles != null && roles.contains("admin"))) {
            queryWrapper.eq("user_name", username);
        }
        Page<SmsRent> page = new Page<SmsRent>(pageNo, pageSize);
        IPage<SmsRent> pageList = this.page(page, queryWrapper);
        List<SmsRent> records = pageList.getRecords();
        if (records.size() > 0) {
            Set<String> collect = records.stream().map(SmsRent::getProjectCode).collect(Collectors.toSet());
            LambdaQueryWrapper<SmsTemplate> templateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            templateLambdaQueryWrapper.in(SmsTemplate::getTemplateCode, collect);
            Map<String, String> map = smsTemplateMapper.selectList(templateLambdaQueryWrapper).stream().collect(Collectors.toMap(SmsTemplate::getTemplateCode, SmsTemplate::getTemplateName));
            records.forEach(e -> {
                e.setProjectName(map.get(e.getProjectCode()) + "(" + e.getProjectCode() + ")");
            });
        }
        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

    @Override
    @Transactional
    public Result<String> wakeup(Long rentId) {
        Integer effect = this.baseMapper.wakeUp(rentId);
        if (effect == 1) {
            SmsRent smsRent = this.baseMapper.selectById(rentId);
            SmsDevice device = smsDeviceMapper.selectById(smsRent.getDeviceId());
            if (Objects.equals(device.getPhone(), smsRent.getPhone())) {
                return Result.ok("唤醒成功");
            }
            throw new RuntimeException("唤醒失败");
        } else {
            throw new RuntimeException("唤醒失败");
        }

    }

    @Override
    @Transactional
    public Result<String> applyApi(String projectCode, Integer num) {
        if (StringUtils.isBlank(projectCode) || "请选择".equals(projectCode)) {
            throw new RuntimeException("请选择申请的项目");
        }
        if (num == null || num.equals(0)) {
            throw new RuntimeException("数量");
        }
        //获取当前操作人
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        //查询项目
        SmsTemplate template = smsTemplateMapper.getByCode(projectCode);
        if (template == null) {
            return null;
        }
        SmsPrice smsPrice = smsPriceMapper.getPriceByUserNameAndProjectCode(username, template.getTemplateCode());
//        if (this.baseMapper.waitCount(username, template.getTemplateCode()) >= Optional.ofNullable(smsPrice).map(SmsPrice::getQuota).orElse(20)) {
//            throw new RuntimeException("当前用户配置的并发额度不足,请联系管理员提升配额");
//        }
        BigDecimal onePrice = Optional.ofNullable(smsPrice).map(SmsPrice::getPrice).orElse(template.getPrice());
        Boolean reduceResult = userService.reduceBySmsPrice(username, onePrice.multiply(new BigDecimal(num)));
        if (!reduceResult) {
            throw new RuntimeException("余额不足");
        }
        String applyCode = UUID.randomUUID().toString();
        String queryRent = String.valueOf(template.getRentType());
        if (template.getOnlyShort()) {
            queryRent = "0";
        } else if (template.getRentType().equals(0)) {
            queryRent = "0,1";
        } else {
            queryRent = "1";
        }
        Integer effect = this.baseMapper.applyApi(projectCode, applyCode, queryRent, username, num);
        if (effect == null || effect == 0 || effect < num) {
            throw new RuntimeException(effect == null || effect == 0 ? "暂无资源" : ("资源不足:剩余" + effect));
        }
        List<SmsDevice> deviceList = this.smsDeviceMapper.getByApplyCodeList(applyCode);
        List<SmsRent> smsRentList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        deviceList.forEach(device -> {
            SmsRent smsRent = new SmsRent();
            smsRent.setPrice(smsPrice == null ? template.getPrice() : smsPrice.getPrice());
            smsRent.setPhone(device.getPhone());
            smsRent.setCreatedTime(LocalDateTime.now());
            smsRent.setUserName(username);
            smsRent.setRentType(template.getRentType());
            smsRent.setProjectCode(template.getTemplateCode());
            smsRent.setApplyCode(applyCode);
            smsRent.setDeviceId(String.valueOf(device.getId()));
            smsRent.setSlotNum(device.getSlotNum());
            smsRent.setDevicePort(device.getDevicePort());
            smsRent.setRentId(SnowFlakeIdGenerator.getInstance().nextId());
            smsRent.setApplyType("link");
            smsRent.setWakeupExpireTime(LocalDateTime.now().plusMinutes(20));
            try {
                buffer.append(device.getPhone() + "----" + "http://api.get-code.top/sms-gateway/smsRentApi/apiGetCode?code=" + AesEncryptUtil.encrypt(String.valueOf(smsRent.getRentId())));
                buffer.append("\n");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (smsRent.getRentType().equals(1)) {
                smsRent.setExpiredTime(LocalDateTime.now().plusDays(30));
            } else {
                smsRent.setExpiredTime(null);
            }
            smsRentList.add(smsRent);
        });
        List<BatchResult> insert = this.baseMapper.insert(smsRentList);
        this.smsDeviceMapper.clearApplyCode(applyCode);
        this.smsTemplateMapper.updateReduce(projectCode);
        SysUser user = userService.getUserByName(username);
        telegramBot.sendToChats(String.format("[用户:%s] 申请号码 项目:[%s] 数量[%s] 单价[%s] 余额[%s] \n\n 批次号[%s]", username, template.getTemplateCode(), num, onePrice.toPlainString(), user.getBalance().toPlainString(), applyCode));

        return Result.ok(buffer.toString());
    }

    public static void main(String[] args) throws Exception {
        System.out.println(hasIntersection("[BankofMelb] app download: https://itunes.apple.com/us/app/bank-melbourne-banking-application/id449512723?ls=1&mt=8. Android app download: https://play.google.com/st","Google"));
    }

    @Override
    @Transactional
    public SmsRent apply(String projectCode) {
        if (StringUtils.isBlank(projectCode) || "请选择".equals(projectCode)) {
            throw new RuntimeException("请选择申请的项目");
        }
        //获取当前操作人
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        //查询项目
        SmsTemplate template = smsTemplateMapper.getByCode(projectCode);
        if (template == null) {
            return null;
        }
        SmsPrice smsPrice = smsPriceMapper.getPriceByUserNameAndProjectCode(username, template.getTemplateCode());
//        if (this.baseMapper.waitCount(username, template.getTemplateCode()) >= Optional.ofNullable(smsPrice).map(SmsPrice::getQuota).orElse(20)) {
//            throw new RuntimeException("当前用户配置的并发额度不足,请联系管理员提升配额");
//        }
        Boolean reduceResult = userService.reduceBySmsPrice(username, smsPrice == null ? template.getPrice() : smsPrice.getPrice());
        if (!reduceResult) {
            throw new RuntimeException("余额不足");
        }
        String applyCode = UUID.randomUUID().toString();
        String queryRent = String.valueOf(template.getRentType());
        if (template.getOnlyShort()) {
            queryRent = "0";
        } else if (template.getRentType().equals(0)) {
            queryRent = "0,1";
        } else {
            queryRent = "1";
        }
        Integer effect = this.baseMapper.apply(projectCode, applyCode, queryRent, username);
        if (effect == null || effect == 0) {
            throw new RuntimeException("暂无资源");
        }
        SmsDevice device = this.smsDeviceMapper.getByApplyCode(applyCode);
        SmsRent smsRent = new SmsRent();
        smsRent.setPrice(smsPrice == null ? template.getPrice() : smsPrice.getPrice());
        smsRent.setPhone(device.getPhone());
        smsRent.setCreatedTime(LocalDateTime.now());
        smsRent.setUserName(username);
        smsRent.setRentType(template.getRentType());
        smsRent.setProjectCode(template.getTemplateCode());
        smsRent.setApplyCode(applyCode);
        smsRent.setDeviceId(String.valueOf(device.getId()));
        smsRent.setSlotNum(device.getSlotNum());
        smsRent.setDevicePort(device.getDevicePort());
        smsRent.setWakeupExpireTime(LocalDateTime.now().plusMinutes(20));
        if (smsRent.getRentType().equals(1)) {
            smsRent.setExpiredTime(LocalDateTime.now().plusDays(30));
        } else {
            smsRent.setExpiredTime(LocalDateTime.now().plusMinutes(20));
        }
        int insert = this.baseMapper.insert(smsRent);
        if (insert == 0) {
            throw new RuntimeException("资源不可用");
        }
//        if (device.getSlotStatus().equals("0/1/1")) {
//            log.info("激活卡槽:{} {} ", device.getDevicePort(), device.getSlotNum());
//            HttpUtils.doGet(device.getDeviceOtherInfo().replace("goip_get_sms", "goip_send_cmd") + "&op=switch&port=" + device.getDeviceId() + NumberToExcelColumn.numberToColumn(Integer.parseInt(device.getSlotNum())));
//        }

        this.smsDeviceMapper.clearApplyCode(applyCode);
        this.smsTemplateMapper.updateReduce(projectCode);
        SysUser user = userService.getUserByName(username);
        telegramBot.sendToChats(String.format("[用户:%s] 申请号码 项目:[%s] 单价[%s] 余额[%s]", username, smsRent.getProjectCode(), smsRent.getPrice().toPlainString(), user.getBalance().toPlainString()));
        return this.baseMapper.getByApplyCode(applyCode);
    }

    @Override
    @Transactional
    public void blackNum(Long rendId) {
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Set<String> roles = commonAPI.queryUserRoles(username);
        SmsRent getSmsRent = this.baseMapper.selectById(rendId);
        Integer effect = 0;
        if (roles != null && roles.contains("admin")) {
            effect = this.baseMapper.blackNumNotUser(rendId);
        } else {
            effect = this.baseMapper.blackNum(rendId, username);
        }
        if (effect != 1) {
            throw new RuntimeException("拉黑失败");
        } else {
            SmsRent smsRent = this.baseMapper.getByRentId(rendId);
            userService.recoveryBalanceBySmsPrice(smsRent.getUserName(), smsRent.getPrice());
            smsTemplateMapper.updateAdd(smsRent.getProjectCode());
            SysUser user = userService.getUserByName(username);
            telegramBot.sendToChats(String.format("[用户:%s] 回收号码 项目:[%s] 单价[%s] 余额[%s]", username, smsRent.getProjectCode(), smsRent.getPrice().toPlainString(), user.getBalance().toPlainString()));
        }
    }

    @Override
    public void done(Long rentId) {
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Integer effect = this.baseMapper.done(rentId, username);
        if (effect != 1) {
            throw new RuntimeException("完结失败");
        }
    }

    @Override
    public void removeBlack(Long rentId) {
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Set<String> roles = commonAPI.queryUserRoles(username);
        if (!(roles != null && roles.contains("admin"))) {
            Integer effect = this.baseMapper.removeBlack(rentId, username);
            if (effect != 1) {
                throw new RuntimeException("移除失败");
            }
        } else {
            Integer effect = this.baseMapper.removeBlackAdmin(rentId);
            if (effect != 1) {
                throw new RuntimeException("移除失败");
            }
        }
    }

    @Override
    public void callbackMC(List<String> data, String username) {
        if ("0".equals(data.get(0))) {

        }
        String[] split = data.get(1).split("\\.");
        String port = split[0];
        String slot = split[1];
        String from = data.get(3);
        AtomicReference<String> content = new AtomicReference<>(new String(Base64.getDecoder().decode(data.get(5).getBytes())).split("\n")[6].replace("-", ""));
        log.info("收到消息 {} {} {} {} {}", port, slot, from ,content, username);
        telegramBot.sendToChats(String.format("收到消息: %s %s %s %s %s", port, slot, from, content, username));
        List<SmsCodeMatchBO> info = this.baseMapper.getRentInfoByWait(port, username, slot);
        if (updatePhone(username, port, slot, content.get())) {
            return;
        }
        content.set("["+from+"]"+content.get());
        AtomicReference<Boolean> match = new AtomicReference<>(false);
        if (!CollectionUtil.isEmpty(info)&&!hasIntersection(content.get().toLowerCase(),"bank")) {
            info.forEach(e -> {
                String templateContent = e.getTemplateContent();
                String[] split2 = templateContent.split(";");
                for (String temp : split2) {
                    if (hasIntersection(content.get().toLowerCase(),temp.toLowerCase())&&!match.get()) {
                        match.set(true);
                        log.info("匹配关键字："+temp.toLowerCase());
                        String[] split1 = e.getCaptchaLength().split("-");
                        int minLength = Integer.parseInt(split1[0]);
                        int maxLength = Integer.parseInt(split1.length != 1 ? split1[1] : split1[0]);
                        String captcha = extractDigitsByLength(content.get().replace("-", ""), minLength, maxLength);
                        if (captcha.length() <= maxLength && captcha.length() >= minLength) {
                            content.set(DateUtils.now() + "|" + content);
                            this.baseMapper.updateCode(e.getRentId(), captcha, content.get());
                        }
                    }
                }
            });
        }
        if (match.get()){
            return;
        }
        List<SmsTemplate> smsTemplates = smsTemplateMapper.selectList(new QueryWrapper<>());
        Boolean noMatch = smsTemplates.stream().noneMatch(e -> {
            String templateContent = e.getTemplateContent();
            String[] split1 = templateContent.split(";");
            for (String temp : split1) {
                if (hasIntersection(content.get().toLowerCase(),temp.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
        if (noMatch||hasIntersection(content.get().toLowerCase(),"bank")) {
            SmsCodeMatchBO bankServices = this.baseMapper.getBankServices(port, username, slot);
            if (bankServices != null) {
                content.set(DateUtils.now() + "|" + content);
                this.baseMapper.updateCode(bankServices.getRentId(), "自行提取", content.get());
            } else {
                telegramBot.sendToChats("孤儿消息:" + content);
            }
        } else {
            telegramBot.sendToChats("孤儿消息2:" + content);
        }
    }


    @Override
    public void callbackMC2(List<String> data, String username) {

        String from = data.get(0);
        log.info("收到消息 {} {} {}", data.get(0), data.get(1), username);
        telegramBot.sendToChats(String.format("收到消息: %s %s %s", data.get(0), data.get(1), username));
        List<SmsCodeMatchBO> info = this.baseMapper.getRentInfoByWait(username, username, username);
        AtomicReference<String> content = new AtomicReference<>("[" + from + "]" + data.get(1));
        AtomicReference<Boolean> match = new AtomicReference<>(false);
        if (!CollectionUtil.isEmpty(info)&&!hasIntersection(content.get().toLowerCase(),"bank")) {
            info.forEach(e -> {
                String templateContent = e.getTemplateContent();
                String[] split2 = templateContent.split(";");
                for (String temp : split2) {
                    if (hasIntersection(content.get().toLowerCase(),temp.toLowerCase())) {
                        match.set(true);
                        String[] split1 = e.getCaptchaLength().split("-");
                        int minLength = Integer.parseInt(split1[0]);
                        int maxLength = Integer.parseInt(split1.length != 1 ? split1[1] : split1[0]);
                        String captcha = extractDigitsByLength(content.get().replace("-", ""), minLength, maxLength);
                        if (captcha.length() <= maxLength && captcha.length() >= minLength) {
                            content.set(DateUtils.now() + "|" + content);
                            this.baseMapper.updateCode(e.getRentId(), captcha, content.get());
                        }
                    }
                }
            });
        }
        if (match.get()){
            return;
        }
        List<SmsTemplate> smsTemplates = smsTemplateMapper.selectList(new QueryWrapper<>());
        Boolean noMatch = smsTemplates.stream().noneMatch(e -> {
            String templateContent = e.getTemplateContent();
            String[] split1 = templateContent.split(";");
            for (String temp : split1) {
                if (hasIntersection(content.get(),temp)) {
                    return true;
                }
            }
            return false;
        });
        if (noMatch||hasIntersection(content.get().toLowerCase(),"bank")) {
            SmsCodeMatchBO bankServices = this.baseMapper.getBankServices(username, username, username);
            if (bankServices != null) {
                content.set(DateUtils.now() + "|" + content);
                this.baseMapper.updateCode(bankServices.getRentId(), "自行提取", content.get());
            } else {
                telegramBot.sendToChats("孤儿消息:" + content);
            }
        } else {
            telegramBot.sendToChats("孤儿消息2:" + content);
        }
    }

    public static boolean hasIntersection(String str1, String str2) {
        str1 = str1.replaceAll("\\."," ").replaceAll(":"," ").replaceAll("\\["," ").replaceAll("\\]"," ");
        str2 = str2.replaceAll("\\."," ").replaceAll(":"," ").replaceAll("\\["," ").replaceAll("\\]"," ");
        // 1. 处理空值/空字符串：空列表无交集
        if (str1 == null || str1.trim().isEmpty() || str2 == null || str2.trim().isEmpty()) {
            return false;
        }

        // 2. 分割字符串：str1按;分割，str2按任意空白符（多个空格/制表符等）分割
        String[] arr1 = str1.split("\\s+");
        String[] arr2 = str2.split(";"); // \\s+ 匹配1个或多个空格/制表符，避免分割出空字符串

        // 3. 转换为Set（去重+提升查找效率，Set的contains是O(1)，List是O(n)）
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        // 过滤空元素（比如分割后出现""），并加入Set
        for (String s : arr1) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                set1.add(trimmed);
            }
        }
        for (String s : arr2) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                set2.add(trimmed);
            }
        }

        // 4. 若任一Set为空，无交集
        if (set1.isEmpty() || set2.isEmpty()) {
            return false;
        }

        // 5. 遍历较小的Set，判断是否有元素在另一个Set中（优化性能）
        Set<String> smallerSet = set1.size() < set2.size() ? set1 : set2;
        Set<String> largerSet = set1.size() < set2.size() ? set2 : set1;

        for (String element : smallerSet) {
            if (largerSet.contains(element)) {
                return true; // 找到交集，立即返回
            }
        }

        // 6. 无交集
        return false;
    }


    @Override
    @Transactional
    public Result<Boolean> addTime(SmsRent smsRent) {
        SmsRent rentInfo = this.baseMapper.selectById(smsRent.getRentId());
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Boolean reduceResult = userService.reduceBySmsPrice(username, rentInfo.getPrice());
        if (!reduceResult) {
            throw new RuntimeException("余额不足");
        }
        Integer effect = this.baseMapper.addTime(smsRent.getRentId(), username);
        if (effect == 0) {
            throw new RuntimeException("续费失败");
        }
        return Result.ok();
    }


    private boolean updatePhone(String username, String port, String slot, String content) {
        if (content.startsWith("TATE?state=NewAccount;server")) {
            String phone = extractDigitsByLength(content, 11, 11);
            Integer effect = smsDeviceMapper.updatePhone(username, port, phone, slot);
            if (effect == 1) {
                telegramBot.sendToChats(String.format("设备[%s] 端口[%s] 卡槽[%s] 更新手机号 [%s] ", username, port, slot, phone));
            }
            return true;
        } else if (content.startsWith("Welcome to Vodafone! Your new number is")) {
            String phone = extractDigitsByLength(content, 10, 10);
            Integer effect = smsDeviceMapper.updatePhone(username, port, "61" + phone.replaceFirst(Pattern.quote("0"), ""), slot);
            if (effect == 1) {
                telegramBot.sendToChats(String.format("设备[%s] 端口[%s] 卡槽[%s] 更新手机号 [%s] ", username, port, slot, phone));
            }
            return true;
        }
        return false;
    }


    private void addDefaultSmsTemplate(List<SmsCodeMatchBO> info, String content) {
        if (content.startsWith("TATE?state=NewAccount;server=")) {

        }
        SmsCodeMatchBO smsCodeMatchBO = new SmsCodeMatchBO();
        smsCodeMatchBO.setTemplateCode("num");
//        smsCodeMatchBO.setCaptchaLength();
    }


    /**
     * 从文本中提取所有指定长度的连续数字
     *
     * @param text   原始文本
     * @param length 需要提取的数字长度（正整数）
     * @return 符合长度的所有数字列表，无匹配则返回空列表
     * @throws IllegalArgumentException 如果长度为非正整数，抛出异常
     */
    public static String extractDigitsByLength(String text, int minLen, int maxLen) {
        // 入参校验
        if (minLen <= 0 || maxLen <= 0) {
            throw new IllegalArgumentException("数字最小/最大长度必须为正整数！");
        }
        if (minLen > maxLen) {
            throw new IllegalArgumentException("最小长度不能大于最大长度！");
        }
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 核心：6-8位匹配正则 \\d{6,8}
        String regex = "\\d{" + minLen + "," + maxLen + "}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 找到第一个匹配项就返回，找不到返回null
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 从文本的键值对中提取指定key后的指定长度数字（如"name=xxx"中的xxx）
     *
     * @param text   原始文本（包含类似key=value的键值对）
     * @param key    目标键（如"name"）
     * @param length 需要提取的数字长度（正整数）
     * @return 符合条件的数字，无匹配则返回null
     * @throws IllegalArgumentException 如果长度为非正整数，抛出异常
     */
    public static String extractDigitsAfterKey(String text, String key, int length) {
        // 校验入参合法性
        if (length <= 0) {
            throw new IllegalArgumentException("数字长度必须为正整数（length > 0）");
        }
        if (text == null || text.isEmpty() || key == null || key.isEmpty()) {
            return null; // 空文本或空key返回null
        }

        // 动态生成正则表达式：key=(\d{length})（例如key=name,length=11时，表达式为name=(\d{11})）
        String regex = key + "(=)(\\d{" + length + "})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 提取匹配的数字（捕获组2）
        if (matcher.find()) {
            return matcher.group(2); // group(2)对应数字部分
        }
        return null;
    }

    public static int countDifferentCharacters(String s1, String s2) {
        if (s1 == null || s2 == null) {
            // 处理null情况，这里假设null与任何字符串的所有字符都不同
            return Math.max(s1 == null ? 0 : s1.length(), s2 == null ? 0 : s2.length());
        }

        int minLength = Math.min(s1.length(), s2.length());
        int differenceCount = 0;

        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                differenceCount++;
            }
        }

        // 注意：此方法不计算长度差异带来的"额外"字符
        return differenceCount;
    }


}
