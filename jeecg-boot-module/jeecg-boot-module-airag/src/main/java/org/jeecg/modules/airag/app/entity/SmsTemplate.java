package org.jeecg.modules.airag.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmsTemplate implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private  Integer id; // 模板ID
    private  String templateContent; // 模板完整文本（如"您的验证码是xxxxxx，5分钟内有效"）
    private  String placeholder; // 验证码占位符（如"xxxxxx"）
    private  Integer captchaLength; // 验证码固定长度（简化匹配，也可支持范围）
    private String  captchaPatternStr;
    @TableField(exist = false)
    private Pattern captchaPattern; // 验证码格式正则（如数字）
    private String templateName;
    private String templateCode;
    private Boolean templateStatus;
    private LocalDateTime createdTime;
    private BigDecimal price;
    private Integer rentType;
    private Integer stock;
    @TableField(exist = false)
    private Integer rentId;
    private Boolean onlyShort;
    public SmsTemplate(){

    }

    public SmsTemplate( Integer rentId,String text, String placeholder, Integer captchaLength, String patternRegex) {
        this.rentId = rentId;
        this.templateContent = text;
        this.placeholder = placeholder;
        this.captchaLength = captchaLength;
        this.captchaPattern = Pattern.compile(patternRegex);
    }

}
