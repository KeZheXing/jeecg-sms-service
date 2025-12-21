package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.entity.SysBalanceRecord;
import org.jeecg.modules.airag.app.entity.SysStatisticsCost;
import org.jeecg.modules.system.service.ISysBalanceRecordService;
import org.jeecg.modules.system.service.ISysStatisticsCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("sys/statistics/cost")
public class SysStatisticsCostController {

    @Autowired
    private ISysStatisticsCostService sysStatisticsCostService;
    @Autowired
    private CommonAPI commonAPI;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SysStatisticsCost>> queryAllPageList(SysStatisticsCost smsRent, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SysStatisticsCost> queryWrapper = QueryGenerator.initQueryWrapper(smsRent, req.getParameterMap());
        queryWrapper.orderByDesc("statistics_day");
        return sysStatisticsCostService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

}
