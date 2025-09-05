package org.jeecg.modules.airag.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.airag.app.entity.AiragApp;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.vo.ChatConversation;

import java.util.List;

/**
 * @Description: AI应用
 * @Author: jeecg-boot
 * @Date:   2025-02-26
 * @Version: V1.0
 */
public interface ConversationRecordsMapper extends BaseMapper<AiragApp> {


    @Select("select * from conversation_records where conversation_key = #{conversationKey} ")
    ChatConversation getByConversationKey(String conversationKey);

    @Insert("insert into conversation_records (id, title, conversation_key, user_name, create_time,update_time,device_code,customer)\n" +
            "value \n" +
            "(#{chat.id} ,#{chat.title},#{key},#{username},now(),now(),#{deviceCode} ,#{customer} )")
    Integer add(@Param("chat") ChatConversation chat, @Param("key") String key,@Param("username") String username,@Param("deviceCode") String deviceCode,@Param("customer") String customer);

    @Update("update conversation_records set title = #{title},update_time = now() where id = #{id} ")
    Integer updateTitle(@Param("id") String id, @Param("title") String title);

    @Select("select * from conversation_records where user_name =#{username} order by  has_reply desc,update_time desc ")
    List<ChatConversation> list(String username);

    @Delete("delete from conversation_records where conversation_key = #{key} ")
    void deleteByKey(String key);

    @Update("update conversation_records set has_reply = 1,conversation_status = 4 where id = #{id} and has_reply = 0")
    Integer reply(String id);

    @Select("select * from conversation_message_records where third_id = #{messageId} ")
    ConversationMessageRecords getByThird(String messageId);

    @Update("update conversation_message_records set message_status = 1 where third_id = #{id} and message_status=0")
    void sent(String messageId);

    @Update("update conversation_message_records set message_status = 2 where third_id = #{id} and message_status in (0,1) ")
    void delivered(String messageId);

    @Update("update conversation_message_records set message_status = 3 where third_id = #{id} and message_status in (0,1) ")
    void failed(String messageId);

    @Select("SELECT * FROM conversation_records WHERE ID =#{conversationId} ")
    ChatConversation getById(String conversationId);

    @Update("update sys_user set reply_task = reply_task + 1 where username = #{username} ")
    void userReply(String username);
}
