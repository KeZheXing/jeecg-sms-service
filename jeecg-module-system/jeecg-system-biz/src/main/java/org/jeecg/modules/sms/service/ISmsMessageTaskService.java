package org.jeecg.modules.sms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.message.entity.SmsMessageTemplate;
import org.jeecg.modules.message.entity.SysMessageTemplate;
import org.jeecg.modules.sms.entity.vo.SmsMessageTaskAddVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface ISmsMessageTaskService extends IService<SmsMessageTask> {

    Result<IPage<SmsMessageTask>> queryPageList(HttpServletRequest req, QueryWrapper<SmsMessageTask> queryWrapper, Integer pageSize, Integer pageNo);


    void add(SmsMessageTaskAddVO addMessageTask);

    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<SmsMessageTemplate> sysMessageTemplateClass);
}
