package org.jeecg.modules.sms.service;

import org.jeecg.modules.sms.entity.SmsMessageTask;

/**
 * @Author: KKKKK
 * @Date: 2025/8/27 2:31
 **/
public interface ISmsChannelService {

    Boolean sendMsg(SmsMessageTask smsMessageTask);

}
