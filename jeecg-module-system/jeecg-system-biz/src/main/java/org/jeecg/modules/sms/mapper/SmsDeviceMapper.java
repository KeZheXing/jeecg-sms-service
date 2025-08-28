package org.jeecg.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.sms.entity.SmsDevice;

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
}
