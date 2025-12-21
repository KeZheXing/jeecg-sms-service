package org.jeecg.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.airag.app.entity.SmsPrice;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:41
 **/
@Mapper
public interface SmsPriceMapper extends BaseMapper<SmsPrice> {

    @Select("select * from sms_price where user_name = #{username} and template_code =#{templateCode} and price_status = 1")
    SmsPrice getPriceByUserNameAndProjectCode(@Param("username") String username,@Param("templateCode") String templateCode);
}
