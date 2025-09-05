package org.jeecg.modules.airag.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.airag.app.entity.SmsDevice;

import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:41
 **/
public interface SmsDeviceMapper extends BaseMapper<SmsDevice> {

    @Select("select * from sms_device where bind_user =#{username} and device_status = 'Y' ")
    List<SmsDevice> getByUserName(String username);

    @Select("select * from sms_device where bind_user = #{userName} AND device_code = #{messageDeviceCode} and device_status='Y' ")
    SmsDevice getByUserNameAndDeviceCode(@Param("userName") String userName,@Param("messageDeviceCode") String messageDeviceCode);

    @Select("select * from sms_device where device_id = #{deviceId} ")
    SmsDevice getByDeviceId(String deviceId);

    @Update("update sms_device set task_num=task_num+1,send=send+1 where device_code = #{deviceCode} ")
    void success(String deviceCode);

    @Update("update sms_device set task_num=task_num+1,failed=failed+1 where device_code = #{deviceCode} ")
    void failed(String deviceCode);

    @Update("update sms_device set receive=receive+1 where device_code = #{deviceCode} ")
    void receive(String deviceCode);

    @Update("update sms_device set device_status=1 where device_code = #{deviceCode} ")
    Integer ok(String deviceCode);

    @Select("select * from sms_device where device_code = #{deviceCode}")
    SmsDevice getByDeviceCode(String deviceCode);

    @Update("update sms_device set device_status=0 where id = #{id} ")
    void stop(Integer id);

    @Update("update sms_device set last_handle_time =now() where device_code = #{deviceCode} ")
    void updateLastHandleTime(String deviceCode);

    @Select("select * from sms_device where (last_handle_time is null or last_handle_time<date_sub(now(),interval `interval` second )) and device_status = 'Y';")
    List<SmsDevice> getEnableDevice();
}
