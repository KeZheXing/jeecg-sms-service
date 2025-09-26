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
public interface ConversationMessageRecordsMapper extends BaseMapper<ConversationMessageRecords> {


    @Select("select * from  conversation_message_records where conversation_id = #{conversationId} ")
    List<ConversationMessageRecords> getByConversationId(String conversationId);

    @Insert("insert into conversation_message_records (conversation_id, topic_id, role, content, datetime,error,device_code,customer,third_id,message_status,user_name,system_notice) \n" +
            "value \n" +
            "(#{conversationId} ,#{topicId} ,#{role} ,#{content} ,now(),#{error},#{deviceCode} ,#{customer},#{thirdId}   ,#{messageStatus} ,#{userName} ,#{systemNotice}  )")
    Integer add(ConversationMessageRecords data);

    @Select("select * from conversation_message_records where device_code order by id desc limit 3")
    List<ConversationMessageRecords> getLastTask(String deviceCode);

    @Select("select m.*\n" +
            "from conversation_message_records m\n" +
            "join sms_device d on d.device_code = m.device_code\n" +
            "where d.device_channel = 2 and m.message_status=0 and m.third_id is not null and m.datetime > date_sub(now(),interval  15 minute );")
    List<ConversationMessageRecords> getCatWaitList();
}
