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



}
