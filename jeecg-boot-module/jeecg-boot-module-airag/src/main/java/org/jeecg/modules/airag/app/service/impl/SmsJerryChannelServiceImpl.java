package org.jeecg.modules.airag.app.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
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

import java.net.URLEncoder;
import java.time.Instant;

/**
 * @Author: KKKKK
 * @Date: 2025/8/27 2:32
 **/
@Component
@Slf4j
public class SmsJerryChannelServiceImpl implements ISmsChannelService {

    @Autowired
    private SmsDeviceMapper smsDeviceMapper;

    @Autowired
    private IAiragChatService chatService;
    private final static String charset = "UTF-8";

    @Override
    public String sendMsgOne(String userName, String deviceCode, String content, String to) {
        SmsDevice smsDevice = smsDeviceMapper.getByUserNameAndDeviceCode(userName, deviceCode);
        return handleSendMsg(content,to,smsDevice.getDeviceUserName(),smsDevice.getDevicePassword(), smsDevice.getDeviceOtherInfo());
    }

    @Override
    public String sendMsg(SmsMessageTask smsMessageTask) {
        SmsDevice device = smsDeviceMapper.getByUserNameAndDeviceCode(smsMessageTask.getUserName(), smsMessageTask.getMessageDeviceCode());
        if (device == null) {
            log.info("设备未维护...");
            return null;
        }

        String thirdId = handleSendMsg(smsMessageTask.getMessageContent(), smsMessageTask.getMessageTo(), device.getDeviceUserName(), device.getDevicePassword(),device.getDeviceOtherInfo());
        addToIM(smsMessageTask,thirdId);
        return thirdId;
    }

    @SneakyThrows
    private String  handleSendMsg(String content, String to, String username, String password, String deviceOtherInfo) {
        Long time = System.currentTimeMillis() / 1000;
        String query = String.format(String.format("http://websiteapi.ssycloud.com/api/v1/sms/sendbatchsms?account=%s&sign=%s&datetime=%s",
                username,DigestUtil.md5Hex((username + password + time)),time));
        JSONObject sendData = new JSONObject();
        sendData.put("content",content);
        sendData.put("numbers",to);
        sendData.put("codeId",deviceOtherInfo);
        sendData.put("action","MARKETING");
        String result = HttpUtils.doPost(query,sendData.toJSONString());
        JSONObject jsonObject = JSON.parseObject(result);
        Integer status = jsonObject.getInteger("status");
        if (status.equals(0)){
            String batchNo = jsonObject.getJSONObject("data").getString("batchNo");
            return batchNo;
        }
        return null;
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
