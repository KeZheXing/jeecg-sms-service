package org.jeecg.modules.airag.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;

import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:41
 **/
public interface SmsMessageTaskMapper extends BaseMapper<SmsMessageTask> {

    @Select("select message_device_code, message_to, message_status, user_name, message_content, t.id\n" +
            "from sms_message_task t\n" +
            "where message_status = 0 and message_device_code= #{deviceCode}\n")
    SmsMessageTask getWaitTask(String deviceCode);

    @Update("update sms_message_task set message_status =1,updated_time=now(),handle_time=now() where id =#{id} ")
    void success(Integer id);

    @Update("update sms_message_task set message_status =2,updated_time=now(),handle_time=now() where id =#{id} ")
    void failed(Integer id);
}
