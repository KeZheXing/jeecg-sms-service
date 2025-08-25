package org.jeecg.modules.airag.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.airag.app.entity.AiragApp;
import org.jeecg.modules.airag.app.vo.ChatConversation;

/**
 * @Description: AI应用
 * @Author: jeecg-boot
 * @Date:   2025-02-26
 * @Version: V1.0
 */
public interface ConversationRecordsMapper extends BaseMapper<AiragApp> {


    @Select("select * from conversation_record where conversation_key = #{conversationKey} ")
    ChatConversation getByConversationKey(String conversationKey);

    @Insert("insert into conversation_records (id, title, conversation_key, user_name, create_time)\n" +
            "value \n" +
            "(#{chat.id} ,#{chat.title},#{key},#{username},now())")
    Integer add(@Param("chat") ChatConversation chat, @Param("key") String key,@Param("username") String username);

    @Update("update conversation_records set title = #{title} where id = #{id} ")
    Integer updateTitle(@Param("id") String id, @Param("title") String title);
}
