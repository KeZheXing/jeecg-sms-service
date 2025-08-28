package org.jeecg.modules.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.service.IAiragChatService;
import org.jeecg.modules.airag.app.vo.ChatSendParams;
import org.jeecg.modules.sms.entity.SmsDevice;
import org.jeecg.modules.sms.entity.SmsMessageTask;
import org.jeecg.modules.sms.mapper.SmsDeviceMapper;
import org.jeecg.modules.sms.service.ISmsChannelService;
import org.jeecg.modules.sms.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @Author: KKKKK
 * @Date: 2025/8/27 2:32
 **/
@Component
@Slf4j
public class SmsCardSendChannelServiceImpl implements ISmsChannelService {

    @Autowired
    private SmsDeviceMapper smsDeviceMapper;

    @Autowired
    private IAiragChatService chatService;

    @Override
    public Boolean sendMsg(SmsMessageTask smsMessageTask) {
        SmsDevice device = smsDeviceMapper.getByUserNameAndDeviceCode(smsMessageTask.getUserName(), smsMessageTask.getMessageDeviceCode());
        if (device == null) {
            log.info("设备未维护...");
            return false;
        }
        addToIM(smsMessageTask);
        handleSendMsg(smsMessageTask,device);
        return true;
    }

    private void handleSendMsg(SmsMessageTask smsMessageTask, SmsDevice device) {
        String query = "https://sms-online.top/api/3rdparty/v1/messages?skipPhoneValidation=true&withDeliveryReport=false";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",smsMessageTask.getMessageContent());
        jsonObject.put("phoneNumbers", Arrays.asList(smsMessageTask.getMessageTo()));
        HttpUtils.doPostByAuth(query, jsonObject.toJSONString(),device.getDeviceUserName(),device.getDevicePassword());
    }

    private void addToIM(SmsMessageTask smsMessageTask) {
        ChatSendParams chatSendParams = new ChatSendParams();
        chatSendParams.setDeviceId(smsMessageTask.getMessageDeviceCode());
        chatSendParams.setContent(smsMessageTask.getMessageContent());
        chatSendParams.setConversationId(smsMessageTask.getMessageDeviceCode()+":"+smsMessageTask.getMessageTo());
        chatSendParams.setFrom(smsMessageTask.getUserName());
        TokenUtils.tempUser.set(smsMessageTask.getUserName());
        chatService.send(chatSendParams);
        TokenUtils.tempUser.remove();
    }
}
