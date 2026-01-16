package org.jeecg.modules.airag.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
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

    @Update("update sms_device set failed=failed+1 where device_code = #{deviceCode} ")
    void failed(String deviceCode);

    @Update("update sms_device set receive=receive+1 where device_code = #{deviceCode} ")
    void receive(String deviceCode);

    @Update("update sms_device set device_status='Y' where device_code = #{deviceCode} ")
    Integer ok(String deviceCode);

    @Select("select * from sms_device where device_code = #{deviceCode}")
    SmsDevice getByDeviceCode(String deviceCode);

    @Select("select * from sms_device where device_other_info = #{deviceOtherInfo}")
    SmsDevice getByDeviceOtherInfo(String deviceOtherInfo);

    @Update("update sms_device set device_status='N' where id = #{id} ")
    void stop(Integer id);

    @Update("update sms_device set last_handle_time =now() where device_code = #{deviceCode} ")
    void updateLastHandleTime(String deviceCode);

    @Select("select * from sms_device where (last_handle_time is null or last_handle_time<date_sub(now(),interval `interval` second )) and device_status = 'Y';")
    List<SmsDevice> getEnableDevice();

    @Update("update sms_device set last_task_id = #{taskId}  where id = #{id} ")
    void updateLastTaskId(@Param("id") Integer id, @Param("taskId") Long taskId);

    @Select("select * from sms_device where device_status = 'Y' and device_channel = '2'")
    List<SmsDevice> getCatDevice();

    @Select("select * from sms_device where apply_code = #{applyCode} ")
    SmsDevice getByApplyCode(String applyCode);

    @Select("select * from sms_device where apply_code = #{applyCode} ")
    List<SmsDevice> getByApplyCodeList(String applyCode);

    @Update("update sms_device set apply_code = null where apply_code = #{applyCode} ")
    void clearApplyCode(String applyCode);

    @Update("update sms_device set apply_code = #{applyCode} where id = #{id} ")
    void updateApplyCode(@Param("id") Integer id, @Param("applyCode") String applyCode);

    @Update("\n" +
            "update sms_device set port_status = #{st} ,notice_time =now() ,sig =#{sig},slot_status = #{soltStatus}\n" +
            "                      where device_code=#{deviceCode}")
    Integer updateDeviceMC(@Param("deviceCode") String deviceCode, @Param("port") String port, @Param("sig") Integer sig, @Param("soltStatus") String soltStatus,@Param("st") String st);

    @Select("select device_other_info from sms_device where device_user_name =#{usernames} limit 1")
    String getMCUrl(String usernames);

    @Update("UPDATE sms_device set phone =#{phone},phone_update_time = now() where device_user_name = #{username} and ( phone!=#{phone} or phone is null)  and device_id = #{port} and slot_num = #{slot} limit 1")
    Integer updatePhone(@Param("username") String username, @Param("port") String port, @Param("phone") String phone,@Param("slot") String slot);

    @Select("select device_other_info,device_user_name from sms_device group by device_other_info,device_user_name;")
    List<SmsDevice> getMCUrlList();

    @Update("update sms_template set stock =0 where update_batch is null or update_batch<> #{uuid} ")
    void clearStock(String uuid);

    @Update("update sms_device set phone = null ,need_active=true , phone_update_time = now() where device_user_name = #{userName} and device_id = #{port} ")
    void clearPhone(@Param("userName") String userName,@Param("port") String port);

    @Insert("insert into sms_device (device_code,device_id,slot_status,port_status,sig,device_user_name,device_password,slot_num,device_port,device_other_info) values (#{deviceCode}  ,#{port} ,#{soltStatus} ,#{st} ,#{sig} ,#{username} ,#{username},#{slotNum},#{devicePort},#{deviceOtherInfo}  )")
    void addDeviceMC(@Param("deviceCode")String deviceCode , @Param("username") String username, @Param("port") String port, @Param("sig") Integer sig, @Param("soltStatus") String soltStatus, @Param("st") String st, @Param("slotNum") String slotNum, @Param("devicePort") String devicePort, @Param("deviceOtherInfo") String deviceOtherInfo);

    @Select("select d.id,d.device_other_info,d.device_port,d.device_user_name,d.device_id,d.slot_num\n" +
            "from sms_device d\n" +
            "where d.slot_status in ('0/1/1') and notice_time>date_sub(now(),interval 5 minute ) and  not exists(select r.rent_id from sms_rent r where r.device_port = d.device_port and r.rent_status=0) and d.device_status='Y' and phone is null and d.need_active=true  \n")
    List<SmsDevice> needUpdatePhoneList();

    @Update("update sms_device set need_active = false where id = #{id}")
    void updateNeedActive(Integer id);

    @Select("select device_user_name,max(notice_time)<date_sub(now(),interval #{dictValue} second ) as `needActive`\n" +
            "from sms_device  group by device_user_name;")
    List<SmsDevice> noticeWarn(String dictValue);

    @Select("select i.item_value\n" +
            "from sys_dict d\n" +
            "join sys_dict_item i on d.id=i.dict_id\n" +
            "where d.dict_name=#{dictName} and i.item_text= #{itemName} limit 1")
    String getDictValue(@Param("dictName") String dictName, @Param("itemName") String itemName);

    @Select("select * from sms_device where phone = #{phone}")
    SmsDevice getByPhone(String phone);

    @Select("select d.*\n" +
            "from sms_rent r\n" +
            "         join sms_device d on r.device_id = d.id\n" +
            "where d.device_user_name = #{deviceUserName} and d.device_id =#{deviceId} and d.slot_num !=#{slotNum} and ((r.rent_status =0 and r.expired_time>now()) or (r.rent_status =0 and r.link_active_expire_time>now())) \n" +
            ";\n")
    SmsDevice isBussiness(@Param("deviceUserName") String deviceUserName,@Param("deviceId") String deviceId,@Param("slotNum") String slotNum);

    @Update("update sms_rent set link_active_expire_time = date_add(now(),interval 5 minute ) where rent_id =#{id} and (link_active_expire_time is null or link_active_expire_time<date_sub(now(),interval  5 minute ))")
    void active(String id);
}
