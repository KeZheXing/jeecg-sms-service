package org.jeecg.modules.airag.app.vo;

import lombok.Data;

import java.util.List;

@Data
public class SmsJerryCallbackRequest {


    private String fromNum;

    private String to;

    private String content;

    private String datetime;

    private String type;

    private Integer cnt;

    private List<List>array;
}
