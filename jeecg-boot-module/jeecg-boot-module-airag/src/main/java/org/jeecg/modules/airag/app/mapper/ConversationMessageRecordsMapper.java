package org.jeecg.modules.airag.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.airag.app.entity.AiragApp;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.vo.ChatConversation;
import org.jeecg.modules.airag.common.vo.MessageHistory;

import java.util.List;

/**
 * @Description: AI应用
 * @Author: jeecg-boot
 * @Date:   2025-02-26
 * @Version: V1.0
 */
public interface ConversationMessageRecordsMapper extends BaseMapper<AiragApp> {


    @Select("select * from  conversation_message_records where conversation_id = #{conversationId} ")
    List<ConversationMessageRecords> getByConversationId(String conversationId);

    @Insert("insert into conversation_message_records (conversation_id, topic_id, role, content, datetime) \n" +
            "value \n" +
            "(#{conversationId} ,#{topicId} ,#{role} ,#{content} ,#{datetime} )")
    Integer add(ConversationMessageRecords data);
}
