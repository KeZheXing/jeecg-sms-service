package org.jeecg.modules.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.request.MCPortStatusRequest;
import org.jeecg.modules.airag.app.service.IConversationMessageRecordsService;
import org.jeecg.modules.airag.app.service.IDeviceService;
import org.jeecg.modules.airag.app.vo.SmsCallbackRequest;
import org.jeecg.modules.airag.app.vo.SmsJerryCallbackRequest;
import org.jeecg.modules.sms.service.ISmsRentService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("sms/device")
public class DeviceController {

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISmsRentService rentService;
    @Autowired
    private IConversationMessageRecordsService conversationMessageRecordsService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsDevice>> queryAllPageList(SmsDevice user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsDevice> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        queryWrapper.orderByDesc("id");
        return deviceService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequestMapping(value = "/messageList", method = RequestMethod.GET)
    public Result<IPage<ConversationMessageRecords>> messageList(ConversationMessageRecords conversationMessageRecords, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<ConversationMessageRecords> queryWrapper = QueryGenerator.initQueryWrapper(conversationMessageRecords, req.getParameterMap());
        queryWrapper.isNull("error");
        queryWrapper.eq("user_name", JwtUtil.getUserNameByToken(req));
        queryWrapper.eq("system_notice",false);
        Result<IPage<ConversationMessageRecords>> iPageResult = conversationMessageRecordsService.queryPageList(req, queryWrapper, pageSize, pageNo);
        return iPageResult;
    }

    @IgnoreAuth
//    @RequestMapping(value = "/callback/jerry", method = RequestMethod.POST)
    public Result<?> callbackJerry(@RequestBody JSONObject jsonObject) {
        log.info("sms callback : {}", jsonObject);
        SmsJerryCallbackRequest smsCallbackRequest = jsonObject.toJavaObject(SmsJerryCallbackRequest.class);
        userService.callbackJeery(smsCallbackRequest);
        return Result.ok();
    }


//    收到验证码:[0, 24.01, 1762700191, Google, , U2VuZGVyOiBHb29nbGUNClJlY2VpdmVyOiAiMjQuMDEiIA0KU01TQzogMzQ2MDcwMDgwNzcNClNDVFM6IDI1MTEwOTE0NTYzMDg4DQpTbG90OiAiMDEiDQoNCkctNjY2MjY2IGlzIHlvdXIgR29vZ2xlIHZlcmlmaWNhdGlvbiBjb2RlLg==, 89610300003433465383, 505038350517698, 86155307]

    @IgnoreAuth
    @RequestMapping(value = "/callbackStatus/mc", method = RequestMethod.POST)
    public Result<?> callbackMCStatus(String username,@RequestBody JSONObject jsonObject) {
        log.info("sms callbackStatus mc: {} {}",username,jsonObject);
        if ("recv-sms".equals(jsonObject.getString("type"))){
            List<String> javaList = jsonObject.getJSONArray("sms").getJSONArray(0).toJavaList(String.class);
            log.info("收到验证码:{}",javaList);
            rentService.callbackMC(javaList,username);
        }else if (jsonObject.getString("etms_usr")!=null){

        }else if (jsonObject.getString("type")!=null&&jsonObject.getString("type").equals("port-status")){
            MCPortStatusRequest status = jsonObject.toJavaObject(MCPortStatusRequest.class);
            deviceService.callbackMCStatus(username, Collections.singletonList(status));
        }  else {
            List<MCPortStatusRequest> status = jsonObject.getJSONArray(    "status").toJavaList(MCPortStatusRequest.class);
            deviceService.callbackMCStatus(username,status);
        }
        return Result.ok();
    }

    @IgnoreAuth
//    @RequestMapping(value = "/callback/yp", method = RequestMethod.POST)
    public Result<?> callback(@RequestBody JSONObject jsonObject) {
        log.info("sms callback : {}", jsonObject);
        SmsCallbackRequest smsCallbackRequest = jsonObject.toJavaObject(SmsCallbackRequest.class);
        userService.callback(smsCallbackRequest);
        return Result.ok();
    }

    @RequestMapping(value = "/debug", method = RequestMethod.POST)
    public Result<?> debug(@RequestBody JSONObject jsonObject) {
        Result<?> result = new Result<SmsDevice>();
        try {
            SmsDevice device = JSON.parseObject(jsonObject.toJSONString(), SmsDevice.class);
            Boolean debug = deviceService.debug(device);
            if (debug){
                result.success("调试成功");
            }else {
                result.error500("调试失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("调试失败");
        }
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SmsDevice> add(@RequestBody JSONObject jsonObject) {
        Result<SmsDevice> result = new Result<SmsDevice>();
        try {
            SmsDevice device = JSON.parseObject(jsonObject.toJSONString(), SmsDevice.class);
            deviceService.save(device);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result<SmsDevice> edit(@RequestBody JSONObject jsonObject) {
        Result<SmsDevice> result = new Result<SmsDevice>();
        try {
            SmsDevice device = JSON.parseObject(jsonObject.toJSONString(), SmsDevice.class);
            deviceService.updateById(device);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SmsDevice> delete(HttpServletRequest request,@RequestParam(name = "id", required = true) String id) {
        Result<SmsDevice> result = new Result<SmsDevice>();
        try {
            deviceService.removeById(id);
            result.success("删除成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败");
        }
        return result;
    }

}
