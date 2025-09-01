package org.jeecg.modules.airag.app.entity;

import lombok.Data;
import org.jeecg.modules.airag.common.vo.MessageHistory;

@Data
public class ConversationMessageRecords extends MessageHistory {

    private Boolean addMessage;

    private Boolean error;

    private String deviceCode;

    private String customer;

    private Integer messageStatus;

    private String thirdId;

}
