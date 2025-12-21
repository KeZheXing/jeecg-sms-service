package org.jeecg.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.airag.app.entity.SmsTemplate;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:41
 **/
@Mapper
public interface SmsTemplateMapper extends BaseMapper<SmsTemplate> {

    @Select("select * from sms_template where template_code =#{projectCode} and template_status = 1")
    SmsTemplate getByCode(String projectCode);

    @Update("update sms_template set stock =#{stock},update_batch= #{uuid}  where template_code=#{templateCode} ")
    void updateStock(@Param("templateCode") String templateCode, @Param("stock") String stock, @Param("uuid") String uuid);

    @Update("update sms_template set stock = stock + 1 where template_code = #{projectCode} ")
    void updateAdd(String projectCode);

    @Update("update sms_template set stock = stock - 1 where template_code = #{projectCode} ")
    void updateReduce(String projectCode);
}
