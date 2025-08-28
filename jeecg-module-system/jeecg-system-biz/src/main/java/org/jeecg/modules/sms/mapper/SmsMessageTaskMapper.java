package org.jeecg.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.sms.entity.SmsDevice;
import org.jeecg.modules.sms.entity.SmsMessageTask;

import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:41
 **/
public interface SmsMessageTaskMapper extends BaseMapper<SmsMessageTask> {

    @Select("select message_device_code,message_to,message_status,user_name,message_content,id from sms_message_task where message_status = 0 group by message_device_code,message_to,message_status,user_name,message_content,id \n ")
    List<SmsMessageTask> getWaitTask();

    @Update("update sms_message_task set message_status =1,updated_time=now(),handle_time=now() where id =#{id} ")
    void success(Integer id);
}
