package org.jeecg.modules.sms.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("msg")
@RestController
@Slf4j
public class SmsCodeController {

    @IgnoreAuth
    @RequestMapping(value = "getTask",method = RequestMethod.GET)
    public Result getTask(String deviceId){
      log.info("请求获取任务:{}",deviceId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId",deviceId);
        jsonObject.put("number","15047810597");
        jsonObject.put("content","Hi bro");
        jsonObject.put("taskId","100");
        return Result.ok(jsonObject);
    }

    @IgnoreAuth
    @RequestMapping(value = "setResult",method = RequestMethod.GET)
    public Result setResult(String deviceId,String status,String taskId){
        log.info("返回结果:{} {} {}",deviceId,status,taskId);
        JSONObject jsonObject = new JSONObject();
        return Result.ok();
    }

    @IgnoreAuth
    @RequestMapping(value = "upload",method = RequestMethod.POST)
    public Result upload(@RequestBody JSONObject jsonObject){
        log.info("上传内容:{}",jsonObject);
        return Result.ok();
    }


}
