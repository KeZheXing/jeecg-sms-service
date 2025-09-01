package org.jeecg.modules.airag.app.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.vo.SmsCallbackRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface IConversationMessageRecordsService extends IService<ConversationMessageRecords> {

    Result<IPage<ConversationMessageRecords>> queryPageList(HttpServletRequest req, QueryWrapper<ConversationMessageRecords> queryWrapper, Integer pageSize, Integer pageNo);


}
