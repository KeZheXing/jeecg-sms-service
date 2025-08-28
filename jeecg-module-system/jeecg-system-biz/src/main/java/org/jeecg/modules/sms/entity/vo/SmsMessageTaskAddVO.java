package org.jeecg.modules.sms.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: KKKKK
 * @Date: 2025/8/26 23:26
 **/

@Data
public class SmsMessageTaskAddVO implements Serializable {

    private String targetNums;

    private String messageContent;

}
