package org.jeecg.modules.airag.app.entity;

import lombok.Data;
import org.jeecg.modules.airag.common.vo.MessageHistory;

@Data
public class ConversationMessageRecords extends MessageHistory {

    private Boolean addMessage;

}
