package org.jeecg.modules.sms.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.SmsTemplate;
import org.jeecg.modules.airag.app.entity.vo.SmsGetProjectList;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface ISmsTemplateService extends IService<SmsTemplate> {

    Result<IPage<SmsTemplate>> queryPageList(HttpServletRequest req, QueryWrapper<SmsTemplate> queryWrapper, Integer pageSize, Integer pageNo);

    Result<List<SmsGetProjectList>> getProjectList();

}
