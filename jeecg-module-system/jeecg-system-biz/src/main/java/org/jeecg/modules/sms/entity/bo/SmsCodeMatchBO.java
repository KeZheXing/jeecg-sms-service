package org.jeecg.modules.sms.entity.bo;

import lombok.Data;

@Data
public class SmsCodeMatchBO {

    private Integer rentId;

    private String templateCode;

    private String templateContent;

    private  String placeholder; // 验证码占位符（如"xxxxxx"）

    private  String captchaLength; // 验证码固定长度（简化匹配，也可支持范围）
    private String  captchaPatternStr;
}
