package org.jeecg.modules.airag.app.vo;

import lombok.Data;

@Data
public class SmsCallbackRequest {


    private String event;

    private String deviceId;

    private String id;

    private Payload payload;

    @Data
    public static class Payload{
        private String messageId;

        private String phoneNumber;

        private String message;
    }
}
