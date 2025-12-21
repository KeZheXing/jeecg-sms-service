package org.jeecg.modules.sms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.sms.service.ISmsRentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("sms/rent")
public class SmsRentController {

    @Autowired
    private ISmsRentService iSmsRentService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsRent>> queryAllPageList(SmsRent smsRent, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsRent> queryWrapper = QueryGenerator.initQueryWrapper(smsRent, req.getParameterMap());
        queryWrapper.orderByDesc("created_time");
        queryWrapper.notIn("rent_status", Arrays.asList(7,8));
        return iSmsRentService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequestMapping(value = "/black/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsRent>> queryBlackAllPageList(SmsRent smsRent, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsRent> queryWrapper = QueryGenerator.initQueryWrapper(smsRent, req.getParameterMap());
        queryWrapper.orderByDesc("created_time");
        queryWrapper.in("rent_status", 7,8);
        return iSmsRentService.queryBlackAllPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequestMapping(value = "/addTime", method = RequestMethod.GET)
    public Result<Boolean> addTime(SmsRent smsRent) {
        return iSmsRentService.addTime(smsRent);
    }

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public Result<SmsRent> apply(@RequestBody SmsRent smsRent, HttpServletRequest req) {
        SmsRent apply = iSmsRentService.apply(smsRent.getProjectCode());
        return apply==null? Result.error("暂无资源"):Result.ok(apply);
    }

    @RequestMapping(value = "/blackNum", method = RequestMethod.POST)
    public Result blackNum(@RequestBody SmsRent smsRent, HttpServletRequest req) {
        iSmsRentService.blackNum(smsRent.getRentId());
        return Result.ok();
    }

    @RequestMapping(value = "/done", method = RequestMethod.POST)
    public Result done(@RequestBody SmsRent smsRent, HttpServletRequest req) {
        iSmsRentService.done(smsRent.getRentId());
        return Result.ok();
    }

    @RequestMapping(value = "/wakeup", method = RequestMethod.POST)
    public Result<String> wakeup(@RequestBody SmsRent smsRent, HttpServletRequest req) {
        return iSmsRentService.wakeup(smsRent.getRentId());
    }

    @RequestMapping(value = "/removeBlack", method = RequestMethod.POST)
    public Result removeBlack(@RequestBody SmsRent smsRent, HttpServletRequest req) {
        iSmsRentService.removeBlack(smsRent.getRentId());
        return Result.ok();
    }


}
