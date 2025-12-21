package org.jeecg.modules.sms.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.api.vo.ResultApi;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.sms.service.ISmsRentApiService;
import org.jeecg.modules.sms.service.ISmsRentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("/smsRentApi")
public class SmsRentApiController {

    @Autowired
    private ISmsRentApiService iSmsRentService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsRent>> queryAllPageList(SmsRent smsRent, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsRent> queryWrapper = QueryGenerator.initQueryWrapper(smsRent, req.getParameterMap());
        queryWrapper.orderByDesc("created_time");
        queryWrapper.ne("rent_status", 7);
        return iSmsRentService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequestMapping(value = "/black/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsRent>> queryBlackAllPageList(SmsRent smsRent, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsRent> queryWrapper = QueryGenerator.initQueryWrapper(smsRent, req.getParameterMap());
        queryWrapper.orderByDesc("created_time");
        queryWrapper.eq("rent_status", 7);
        return iSmsRentService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @IgnoreAuth
    @RequestMapping(value = "/apply", method = RequestMethod.GET)
    public ResultApi<JSONObject> apply(SmsRent smsRent, HttpServletRequest req) {
        return iSmsRentService.apply(smsRent.getProjectCode(), smsRent.getApiToken());
    }

    @IgnoreAuth
    @RequestMapping(value = "/blackNum", method = RequestMethod.GET)
    public ResultApi blackNum(SmsRent smsRent, HttpServletRequest req) {
        return iSmsRentService.blackNum(smsRent.getRentId(),smsRent.getApiToken());
    }

    @IgnoreAuth
    @RequestMapping(value = "/getCode", method = RequestMethod.GET)
    public ResultApi getCode(SmsRent smsRent, HttpServletRequest req) {
        return iSmsRentService.getCode(smsRent.getRentId(),smsRent.getApiToken());
    }

    @IgnoreAuth
    @RequestMapping(value = "/done", method = RequestMethod.GET)
    public ResultApi done(SmsRent smsRent, HttpServletRequest req) {
        return iSmsRentService.done(smsRent.getRentId(),smsRent.getApiToken());
    }

    @RequestMapping(value = "/wakeup", method = RequestMethod.POST)
    public Result<String> wakeup(@RequestBody SmsRent smsRent, HttpServletRequest req) {
        return iSmsRentService.wakeup(smsRent.getRentId());
    }

    @IgnoreAuth
    @RequestMapping(value = "/removeBlack", method = RequestMethod.POST)
    public Result removeBlack(SmsRent smsRent, HttpServletRequest req) {
        iSmsRentService.removeBlack(smsRent.getRentId());
        return Result.ok();
    }


}
