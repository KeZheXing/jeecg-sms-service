package org.jeecg.modules.airag.app.entity;

import lombok.Data;

/**
 * @Author: KKKKK
 * @Date: 2024/2/18 21:41
 **/
@Data
public class TgMessage {

    private String message;

    private Boolean atBot;

    private Integer msgId;

    private Long fromUser;

    private Boolean fromBot;

    private Long chatId;

    private Boolean isGroup;

    private String groupName;
}
