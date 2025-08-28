package org.jeecg.modules.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.sms.entity.SmsDevice;
import org.jeecg.modules.sms.service.IDeviceService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsDevice>> queryAllPageList(SmsDevice user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsDevice> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        return deviceService.queryPageList(req, queryWrapper, pageSize, pageNo);
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

}
