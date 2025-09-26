package org.jeecg.modules.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.airag.app.consts.IMConstants;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.airag.app.mapper.ConversationMessageRecordsMapper;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.airag.app.service.IAiragChatService;
import org.jeecg.modules.airag.app.service.impl.SmsCardSendChannelServiceImpl;
import org.jeecg.modules.airag.app.service.impl.SmsJerryChannelServiceImpl;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.airag.app.vo.SmsCallbackRequest;
import org.jeecg.modules.message.service.ICatCheckJobService;
import org.jeecg.modules.message.service.IJobService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class CatCheckStatusServiceImpl implements ICatCheckJobService {


    @Autowired
    private ISysUserService userService;
    @Autowired
    private ConversationMessageRecordsMapper conversationMessageRecordsMapper;
    private final ReentrantLock lock = new ReentrantLock();


    @Transactional
    @Override
    public void checkStatus() {
        boolean lockResult = lock.tryLock();
        if (!lockResult){
            return;
        }
        try {
            synchronized (this) {
                List<ConversationMessageRecords> catWaitList = conversationMessageRecordsMapper.getCatWaitList();
                catWaitList.forEach(messageRecords -> {
                    String result = HttpUtils.doGet("http://sms-online.top:11186/api.jsp?act=getSendStatus&ID=" + messageRecords.getThirdId());
                    JSONObject jsonObject = JSON.parseObject(result);
                    SmsCallbackRequest request = new SmsCallbackRequest();
                    SmsCallbackRequest.Payload payload = new SmsCallbackRequest.Payload();
                    payload.setMessageId(messageRecords.getThirdId());
                    request.setPayload(payload);
                    request.setId(UUID.randomUUID().toString());
                    String status = jsonObject.getString("Status");
                    request.setDeviceId(messageRecords.getDeviceCode());
                    if (status.equals("2")){
                        request.setEvent(IMConstants.SMS_DELIVERED);
                        userService.callback(request);
                    }else if (status.equals("3")){
                        request.setEvent(IMConstants.SMS_FAILED);
                        userService.callback(request);
                    }else if (status.equals("1")){
                        request.setEvent(IMConstants.SMS_sent);
                        userService.callback(request);
                    }

                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    private void addHandleTask(String userName) {
        userService.addHandleTask(userName);
    }

    private void recoveryBalance(String username) {
        userService.recoveryBalance(username);
    }

}
