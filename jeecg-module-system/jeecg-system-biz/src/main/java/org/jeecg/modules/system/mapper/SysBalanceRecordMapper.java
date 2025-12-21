package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.airag.app.entity.SysBalanceRecord;
import org.jeecg.modules.quartz.entity.ListenerRecord;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Mapper
public interface SysBalanceRecordMapper extends BaseMapper<SysBalanceRecord> {

    @Insert("insert into sys_balance_record (user_name,change_balance,wait_balance,record_day,created_time,record_status) value (#{username},#{amount},#{waitAmount} ,#{now} ,now(),1)")
    void createOrder(@Param("username") String username,@Param("amount") BigDecimal amount,@Param("waitAmount") BigDecimal waitAmount,@Param("now") LocalDate now);

    @Select("select * from sys_balance_record where user_name = #{username} and wait_balance= #{amount} and record_day =#{now} ")
    SysBalanceRecord getOrder(@Param("username") String username,@Param("amount") BigDecimal amount, @Param("now") LocalDate now);

    @Select("select * from sys_balance_record where hash_code = #{transactionId} ")
    SysBalanceRecord getByTransId(String transactionId);

    @Select("select * from sys_balance_record where  wait_balance= #{payAmount} and record_day =#{now}  ")
    SysBalanceRecord getRecordIdByPayAmount(@Param("payAmount") BigDecimal payAmount,@Param("now") LocalDate now);

    @Update("update sys_balance_record   set  record_status = 0 ,hash_code =#{hashCode}  where record_id = #{recordId} ")
    Integer updateHash(SysBalanceRecord record);

    @Update("update sys_balance_record   set balance =#{balance}  where record_id = #{recordId} ")
    void updateBalance(@Param("recordId") Integer recordId, @Param("balance") BigDecimal balance);
}
