package org.jeecg.modules.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.airag.app.entity.SmsPrice;
import org.jeecg.modules.sms.service.ISmsPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("sms/price")
public class SmsPriceController {

    @Autowired
    private ISmsPriceService templateService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsPrice>> queryAllPageList(SmsPrice smsTemplate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsPrice> queryWrapper = QueryGenerator.initQueryWrapper(smsTemplate, req.getParameterMap());
        queryWrapper.orderByDesc("id");
        return templateService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SmsPrice> add(@RequestBody JSONObject jsonObject) {
        Result<SmsPrice> result = new Result<SmsPrice>();
        try {
            SmsPrice smsTemplate = JSON.parseObject(jsonObject.toJSONString(), SmsPrice.class);
            smsTemplate.setCreatedTime(LocalDateTime.now());
            templateService.save(smsTemplate);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SmsPrice> edit(@RequestBody JSONObject jsonObject) {
        Result<SmsPrice> result = new Result<SmsPrice>();
        try {
            SmsPrice device = JSON.parseObject(jsonObject.toJSONString(), SmsPrice.class);
            templateService.updateById(device);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SmsPrice> delete(HttpServletRequest request,@RequestParam(name = "id", required = true) String id) {
        Result<SmsPrice> result = new Result<SmsPrice>();
        try {
            templateService.removeById(id);
            result.success("删除成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败");
        }
        return result;
    }

}
