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
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.sms.service.ISmsRentService;
import org.jeecg.modules.system.service.ISysBalanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("sys/balance/record")
public class SysBalanceRecordController {

    @Autowired
    private ISysBalanceRecordService iSysBalanceRecordService;
    @Autowired
    private CommonAPI commonAPI;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SysBalanceRecord>> queryAllPageList(SysBalanceRecord smsRent, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SysBalanceRecord> queryWrapper = QueryGenerator.initQueryWrapper(smsRent, req.getParameterMap());
        queryWrapper.orderByDesc("created_time");
        queryWrapper.eq("record_status",0);
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Set<String> roles = commonAPI.queryUserRoles(username);
        if (!roles.contains("admin")) {
            queryWrapper.eq("user_name",username);
        }
        return iSysBalanceRecordService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

}
