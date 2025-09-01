package org.jeecg.modules.message.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.airag.app.service.ISmsChannelService;
import org.jeecg.modules.message.service.IJobService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class JobServiceImpl implements IJobService {

    @Autowired
    private SmsMessageTaskMapper smsMessageTaskMapper;

    @Autowired
    private ISmsChannelService iSmsChannelService;

    @Autowired
    private ISysUserService userService;
    @Autowired
    private SmsDeviceMapper deviceMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Transactional
    @Override
    public void sendMsgJob() {
        List<SmsMessageTask> waitTask = smsMessageTaskMapper.getWaitTask();
        waitTask.forEach(smsMessageTask -> {
            String thirdId = iSmsChannelService.sendMsg(smsMessageTask);
            if (thirdId!=null){
                smsMessageTaskMapper.success(smsMessageTask.getId());
                addHandleTask(smsMessageTask.getUserName());
                deviceMapper.success(smsMessageTask.getMessageDeviceCode());
            }else {
                smsMessageTaskMapper.failed(smsMessageTask.getId());
                recoveryBalance(smsMessageTask.getUserName());
                deviceMapper.failed(smsMessageTask.getMessageDeviceCode());
                log.info(String.format("退还费用[%s]", smsMessageTask.getUserName()));
                this.recoveryBalance(smsMessageTask.getUserName());
                redisUtil.incr(smsMessageTask.getMessageDeviceCode(),1);
            }
        });
        log.info(String.format(" Jeecg-Boot 发送消息任务 SendMsgJob !  时间:" + DateUtils.getTimestamp()));
    }

    private void addHandleTask(String userName) {
        userService.addHandleTask(userName);
    }

    private void recoveryBalance(String username) {
        userService.recoveryBalance(username);
    }

}
