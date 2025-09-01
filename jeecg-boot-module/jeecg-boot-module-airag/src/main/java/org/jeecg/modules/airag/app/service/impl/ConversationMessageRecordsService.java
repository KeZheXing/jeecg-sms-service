package org.jeecg.modules.airag.app.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.mapper.ConversationMessageRecordsMapper;
import org.jeecg.modules.airag.app.service.IConversationMessageRecordsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
@Slf4j
public class ConversationMessageRecordsService extends ServiceImpl<ConversationMessageRecordsMapper, ConversationMessageRecords> implements IConversationMessageRecordsService {

    @Override
    public Result<IPage<ConversationMessageRecords>> queryPageList(HttpServletRequest req, QueryWrapper<ConversationMessageRecords> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<ConversationMessageRecords>> result = new Result<IPage<ConversationMessageRecords>>();

        //TODO 外部模拟登陆临时账号，列表不显示

        Page<ConversationMessageRecords> page = new Page<ConversationMessageRecords>(pageNo, pageSize);
        IPage<ConversationMessageRecords> pageList = this.page(page, queryWrapper);

        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

}
