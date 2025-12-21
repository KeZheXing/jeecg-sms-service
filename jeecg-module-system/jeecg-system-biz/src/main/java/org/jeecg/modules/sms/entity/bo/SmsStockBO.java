package org.jeecg.modules.sms.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SmsStockBO {

    private String templateCode;

    private String templateName;

    private BigDecimal price;

    private String count;
}
