package org.jeecg.modules.sms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.sms.entity.SmsDevice;
import org.jeecg.modules.sms.entity.SmsMessageTask;
import org.jeecg.modules.sms.entity.vo.SmsMessageTaskAddVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface ISmsMessageTaskService extends IService<SmsMessageTask> {

    Result<IPage<SmsMessageTask>> queryPageList(HttpServletRequest req, QueryWrapper<SmsMessageTask> queryWrapper, Integer pageSize, Integer pageNo);


    void add(SmsMessageTaskAddVO addMessageTask);
}
