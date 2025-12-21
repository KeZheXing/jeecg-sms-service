package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.airag.app.entity.SysBalanceRecord;
import org.jeecg.modules.airag.app.entity.SysStatisticsCost;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Mapper
public interface SysStatisticsCostMapper extends BaseMapper<SysStatisticsCost> {

    @Insert("replace into sys_statistics_cost (user_name,template_code,template_name,statistics_day,statistics_count,statistics_cost,statistics_price) values  (#{userName} ,#{templateCode} ,#{templateName} ,#{statisticsDay} ,#{statisticsCount} ,#{statisticsCost} ,#{statisticsPrice} )")
    void add(SysStatisticsCost e);
}
