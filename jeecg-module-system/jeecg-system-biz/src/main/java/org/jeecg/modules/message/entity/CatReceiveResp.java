package org.jeecg.modules.message.entity;

import lombok.Data;

@Data
public class CatReceiveResp {

    private String ID;

    private String Time;

    private String Port;

    private String phone;

    private String From;

    private String Message;

}
