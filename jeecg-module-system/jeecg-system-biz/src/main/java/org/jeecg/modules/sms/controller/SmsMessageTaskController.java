package org.jeecg.modules.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.message.entity.SmsMessageTemplate;
import org.jeecg.modules.message.entity.SysMessageTemplate;
import org.jeecg.modules.sms.entity.vo.SmsMessageTaskAddVO;
import org.jeecg.modules.sms.service.ISmsMessageTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:28
 **/
@Slf4j
@RestController
@RequestMapping("sms/message")
public class SmsMessageTaskController {

    @Autowired
    private ISmsMessageTaskService smsMessageTaskService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SmsMessageTask>> queryAllPageList(SmsMessageTask user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SmsMessageTask> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        return smsMessageTaskService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SmsDevice> add(@RequestBody JSONObject jsonObject) {
        Result<SmsDevice> result = new Result<SmsDevice>();
        try {
            SmsMessageTaskAddVO addMessageTask = JSON.parseObject(jsonObject.toJSONString(), SmsMessageTaskAddVO.class);
            smsMessageTaskService.add(addMessageTask);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败[%s]".formatted(e.getMessage()));
        }
        return result;
    }


    /**
     * excel导入
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return smsMessageTaskService.importExcel(request, response, SmsMessageTemplate.class);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result<SmsDevice> edit(@RequestBody JSONObject jsonObject) {
        Result<SmsDevice> result = new Result<SmsDevice>();
        try {
            SmsMessageTask device = JSON.parseObject(jsonObject.toJSONString(), SmsMessageTask.class);
            smsMessageTaskService.updateById(device);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

}
