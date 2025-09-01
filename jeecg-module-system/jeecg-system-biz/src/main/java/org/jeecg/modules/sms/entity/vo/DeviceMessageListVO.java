package org.jeecg.modules.sms.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceMessageListVO {

    private String deviceCode;

    private String customer;

    private Date datetime;

    private String messageStatus;

}
