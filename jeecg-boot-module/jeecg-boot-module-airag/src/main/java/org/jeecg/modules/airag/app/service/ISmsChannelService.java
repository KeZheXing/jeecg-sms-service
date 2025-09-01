package org.jeecg.modules.airag.app.service;

import org.jeecg.modules.airag.app.entity.SmsMessageTask;

/**
 * @Author: KKKKK
 * @Date: 2025/8/27 2:31
 **/
public interface ISmsChannelService {

    String sendMsgOne(String userName, String deviceCode, String content, String to);

    String sendMsg(SmsMessageTask smsMessageTask);

}
