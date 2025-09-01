package org.jeecg.modules.airag.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.service.IAiragChatService;
import org.jeecg.modules.airag.app.service.ISmsChannelService;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.vo.ChatSendParams;
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
    public String sendMsgOne(String userName, String deviceCode, String content, String to) {
        SmsDevice smsDevice = smsDeviceMapper.getByUserNameAndDeviceCode(userName, deviceCode);
        return handleSendMsg(content,to,smsDevice.getDeviceUserName(),smsDevice.getDevicePassword());
    }

    @Override
    public String sendMsg(SmsMessageTask smsMessageTask) {
        SmsDevice device = smsDeviceMapper.getByUserNameAndDeviceCode(smsMessageTask.getUserName(), smsMessageTask.getMessageDeviceCode());
        if (device == null) {
            log.info("设备未维护...");
            return null;
        }

        String thirdId = handleSendMsg(smsMessageTask.getMessageContent(), smsMessageTask.getMessageTo(), device.getDeviceUserName(), device.getDevicePassword());
        addToIM(smsMessageTask,thirdId);
        return thirdId;
    }

    private String  handleSendMsg(String content,String to, String username,String password) {
        String query = "https://sms-online.top/api/3rdparty/v1/messages?skipPhoneValidation=true&withDeliveryReport=false";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",content);
        jsonObject.put("phoneNumbers", Arrays.asList(to));
        String result = HttpUtils.doPostByAuth(query, jsonObject.toJSONString(), username, password);
        return JSON.parseObject(result).getString("id");
    }

    private void addToIM(SmsMessageTask smsMessageTask, String thirdId) {
        ChatSendParams chatSendParams = new ChatSendParams();
        chatSendParams.setDeviceId(smsMessageTask.getMessageDeviceCode());
        chatSendParams.setContent(smsMessageTask.getMessageContent());
        chatSendParams.setConversationId(smsMessageTask.getMessageDeviceCode()+":"+smsMessageTask.getMessageTo());
        chatSendParams.setFrom(smsMessageTask.getUserName());
        chatSendParams.setThirdId(thirdId);
        TokenUtils.tempUser.set(smsMessageTask.getUserName());
        chatService.send(chatSendParams);
        TokenUtils.tempUser.remove();
    }
}
