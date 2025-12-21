package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.SysBalanceRecord;
import org.jeecg.modules.airag.app.entity.SysStatisticsCost;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 系统评论回复表
 * @Author: jeecg-boot
 * @Date: 2022-07-19
 * @Version: V1.0
 */
public interface ISysStatisticsCostService extends IService<SysStatisticsCost> {


    Result<IPage<SysStatisticsCost>> queryPageList(HttpServletRequest req, QueryWrapper<SysStatisticsCost> queryWrapper, Integer pageSize, Integer pageNo);

}
