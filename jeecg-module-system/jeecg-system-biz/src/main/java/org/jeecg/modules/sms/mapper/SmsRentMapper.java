package org.jeecg.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.airag.app.entity.SysStatisticsCost;
import org.jeecg.modules.sms.entity.bo.SmsCodeMatchBO;
import org.jeecg.modules.sms.entity.bo.SmsStockBO;

import java.util.Date;
import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:41
 **/
@Mapper
public interface SmsRentMapper extends BaseMapper<SmsRent> {

    @Update("update sms_device d\n" +
            "set d.apply_code = #{code}\n" +
            "where d.rent_type in (${rentType})  and d.device_status='Y'  and d.apply_code is null and (d.phone is not null and d.phone !='0') and d.slot_status in ('1/1/1','0/1/1') and sig>5 \n" +
            " and not exists(select rent_id from sms_rent r where r.device_port = d.device_port and rent_status = 0 and r.slot_num !=d.slot_num )" +
            " and not exists(select rent_id from sms_rent r where r.phone = d.phone  and ( (r.user_name = #{username} and r.rent_status in (7,8) ) or (r.rent_status in (0,97,98,99)  ) )and r.project_code=#{projectCode} )" +
            " order by rand() limit 1")
    Integer apply(@Param("projectCode") String projectCode, @Param("code") String code, @Param("rentType") String rentType,@Param("username") String username);


    @Select("select *from sms_rent where apply_code = #{code} ")
    SmsRent getByApplyCode(String code);

    @Update("update sms_rent set rent_status = 7 ,blacked_time = now() where rent_id = #{rentId} and code is null   and user_name = #{username} and rent_status =0" )
    Integer blackNum(@Param("rentId") Integer rendId,@Param("username") String username);


    @Update("update sms_rent set rent_status = 99 where rent_id = #{rentId}  and user_name = #{username} and rent_status =0 and code is not null")
    Integer done(@Param("rentId") Integer rendId,@Param("username") String username);

    @Select("select * from sms_rent where rent_id =#{rendId} ")
    SmsRent getByRentId(Integer rendId);

    @Delete("delete  from sms_rent where rent_id = #{rentId}  and rent_status  in (7,8)")
    Integer removeBlack(@Param("rentId") Integer rendId,@Param("username") String username);


    @Delete("delete  from sms_rent where rent_id = #{rentId}  and rent_status  in (7,8)")
    Integer removeBlackAdmin(@Param("rentId") Integer rendId);

    @Select("select * from sms_rent  where rent_status =0 and expired_time <now()")
    List<SmsRent> toBlock();

    @Delete("delete from sms_rent where rent_status =8  and rent_id #{rentId}  ")
    void blackToDel(Integer rentId);

    @Select("select count(1) from sms_rent where user_name = #{username} and project_code = #{templateCode}  and rent_status = 0")
    Integer waitCount(@Param("username") String username, @Param("templateCode") String templateCode);

    @Select("select r.rent_id,t.template_code,t.template_content,t.template_name,t.placeholder,t.captcha_length,t.captcha_pattern_str\n" +
            "from sms_rent r\n" +
            "join sms_device d on r.device_id = d.id\n" +
            "join sms_template t on t.template_code = r.project_code\n" +
            "where ((r.rent_status in (0,98,99) and (r.expired_time>now() or (r.wakeup_expire_time is not null and wakeup_expire_time>now())))) and d.device_user_name = #{username}  and d.device_id = #{port} ;")
    List<SmsCodeMatchBO> getRentInfoByWait(@Param("port") String port, @Param("username") String username);

    @Update("update sms_rent set code =concat_ws('\n\n',code,#{code} ),content=concat_ws('\n\n',content,#{content}),wakeup_expire_time =null,rent_status = 99,receive_time = now() where rent_id = #{rentId} ")
    void updateCode(@Param("rentId") Integer rentId,@Param("code") String code,@Param("content") String content);

    @Select("select t.template_code,t.template_name,t.price,count(1) as `count` \n" +
            "            from sms_template t\n" +
            "            join sms_device d  on t.rent_type = d.rent_type\n" +
            "            where t.only_short = 1 and t.rent_type =0 and d.rent_type= 0 and d.device_status='Y' and d.slot_status in ('1/1/1','0/1/1')  and d.phone is not null and d.phone !='0'  and not exists(select r.rent_id from sms_rent r where r.rent_type=t.rent_type and r.rent_status in (0,97,98,99) and r.project_code=t.template_code and r.phone = d.phone) group by t.template_code,t.template_name,t.price;")
    List<SmsStockBO> getStock1();

    @Select("select t.template_code,t.template_name,t.price,count(1) as `count`\n" +
            "            from sms_template t\n" +
            "            join sms_device d  on t.rent_type = d.rent_type\n" +
            "            where t.only_short = 0 and t.rent_type =1 and d.rent_type=1 and d.device_status='Y' and d.slot_status in ('1/1/1','0/1/1')   and d.phone is not null and d.phone !='0' and not exists(select r.rent_id from sms_rent r where r.rent_type=t.rent_type and r.rent_status in (0,97,98,99) and r.project_code=t.template_code and r.phone = d.phone) group by t.template_code,t.template_name,t.price;")
    List<SmsStockBO> getStock2();

    @Select("select t.template_code,t.template_name,t.price,count(1) `count`\n" +
            "            from sms_template t\n" +
            "            join sms_device d " +
            "            where t.only_short = 0 and t.rent_type =0 and d.rent_type in (0,1) and d.device_status='Y' and d.slot_status in ('1/1/1','0/1/1')   and d.phone is not null and d.phone !='0' and not exists(select r.rent_id from sms_rent r where r.rent_type=t.rent_type and r.rent_status in (0,97,98,99) and r.project_code=t.template_code and r.phone = d.phone) group by t.template_code,t.template_name,t.price")
    List<SmsStockBO> getStock3();


    @Select("select * from sms_rent where rent_id = #{rentId}  and user_name =#{username} ")
    SmsRent getByRentIdAndUserName(@Param("rentId") Integer rentId, @Param("username") String username);


    @Update("update sms_rent set expired_time = date_add(expired_time,interval  30 day ) where rent_id = #{rentId} and user_name =#{username} and rent_status in (97,98,99) limit 1")
    Integer addTime(@Param("rentId") Integer rentId, @Param("username") String username);

    @Select("select * from sms_rent  where rent_status in (7,8) and expired_time < date_sub(now(),interval 12 hour)")
    List<SmsRent> toBlock2();

    @Update("update sms_rent set rent_status = 8 where rent_id = #{rentId} and rent_status =0 ")
    void updateToBlack(Integer rentId);

    @Select("select user_name,project_code as 'templateCode',(select template_name from sms_template t where t.template_code = r.project_code limit 1) as 'templateName',count(1) as statisticsCount,SUM(r.price) as 'statisticsCost',r.price as 'statisticsPrice'" +
            ", DATE(#{todayStart}) as 'statisticsDay'" +
            "from sms_rent r  WHERE\n" +
            " r.created_time>=#{todayStart} and r.created_time<#{todayEnd}  and r.rent_status in (97,98,99) GROUP BY user_name,project_code,r.price")
    List<SysStatisticsCost> getStatisticsCost(@Param("todayStart") Date todayStart, @Param("todayEnd") Date todayEnd);

    @Update("update sms_rent set rent_status = 7 ,blacked_time = now() where rent_id = #{rentId} and code is null  and rent_status =0")
    Integer blackNumNotUser(Integer rendId);

    @Update("UPDATE sms_rent r\n" +
            "SET r.rent_status        = 98,\n" +
            "    r.wakeup_expire_time = date_add(now(), INTERVAL 15 minute)\n" +
            "WHERE r.rent_id = #{rentId} \n" +
            "  AND r.rent_status = 99\n" +
            "  AND NOT EXISTS ( SELECT rent_id\n" +
            "    FROM (SELECT r2.rent_id\n" +
            "                  FROM sms_rent r2\n" +
            "                  WHERE r2.device_port = r.device_port\n" +
            "                    AND r2.rent_status IN (0, 98)\n" +
            "                    AND r.slot_num != r2.slot_num) a);")
    Integer wakeUp(Integer rentId);

    @Select("select * from sms_rent where device_port = #{devicePort} and rent_status in (98,0) limit 1")
    SmsRent getBusyNum(String devicePort);

    @Update("update sms_rent set code = null,content =null where receive_time < date_sub(now(),interval 2 hour ) and rent_status =99")
    void clearCode();

    @Update("update sms_rent set rent_status = 99,wakeup_expire_time=null  where wakeup_expire_time<now() and rent_status = 98")
    Integer toBlock3();

    @Update("update sms_rent r\n" +
            "set r.rent_status=98,apply_code = #{uuid}, \n" +
            "    wakeup_expire_time=date_add(now(), interval 15 minute)\n" +
            "where r.rent_status = 97\n" +
            "  and not exists(select rent_id from (select r2.rent_id from sms_rent r2 where r.device_port = r2.device_port and r2.rent_status in (0, 98))a) limit 1")
    Integer toWakeUp(String uuid);


    @Update("update sms_rent set rent_status = 97 where rent_status = 99 and rent_id =#{rentId} ")
    void waitWakeUp(Integer rentId);
}
