package org.jeecg.modules.message.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.airag.app.mapper.ConversationMessageRecordsMapper;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.airag.app.service.IAiragChatService;
import org.jeecg.modules.airag.app.service.ISmsChannelService;
import org.jeecg.modules.airag.app.service.impl.SmsCardSendChannelServiceImpl;
import org.jeecg.modules.airag.app.service.impl.SmsCatChannelServiceImpl;
import org.jeecg.modules.airag.app.service.impl.SmsJerryChannelServiceImpl;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.message.service.IJobService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class JobServiceImpl implements IJobService {

    @Autowired
    private SmsMessageTaskMapper smsMessageTaskMapper;
    @Autowired
    private SmsCardSendChannelServiceImpl smsCardSendChannelService;
    @Autowired
    private SmsCatChannelServiceImpl smsCatChannelService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SmsDeviceMapper deviceMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SmsJerryChannelServiceImpl smsJerryChannelService;
    @Autowired
    private TelegramBot.MyTelegramBot telegramBot;
    @Autowired
    private IAiragChatService chatService;
    @Autowired
    private ConversationMessageRecordsMapper conversationMessageRecordsMapper;
    private final ReentrantLock lock = new ReentrantLock();
    @Transactional
    @Override
    public void sendMsgJob() {
        boolean lockResult = lock.tryLock();
        if (!lockResult){
            return;
        }
        List<SmsDevice> deviceList = null;
        try {
            synchronized (this) {
                deviceList = deviceMapper.getEnableDevice();
                deviceList.forEach(device -> {
                    List<ConversationMessageRecords> lastTask = conversationMessageRecordsMapper.getLastTask(device.getDeviceCode());
                    long count = lastTask.stream().filter(e -> e.getMessageStatus().equals(0)).count();
                    if (count==3){
                        telegramBot.sendToChats(String.format("设备 [%s] 离线！！！！！", device.getDeviceCode()));
                        chatService.systemSend(device.getBindUser(), String.format("设备 [%s] 离线！！！！！", device.getDeviceCode()));
                    }
                    if (count >= 3) {
                        log.info(String.format("设备[%s]暂停执行任务", device.getDeviceCode()));
                        return;
                    }
                    SmsMessageTask smsMessageTask = smsMessageTaskMapper.getWaitTask(device.getDeviceCode());
                    if (smsMessageTask == null) {
                        return;
                    }
                    try {
                        String thirdId = null;
                        if (device.getDeviceChannel().equals("0")) {
                            thirdId = smsCardSendChannelService.sendMsg(smsMessageTask);
                        } else if (device.getDeviceChannel().equals("1")) {
                            thirdId = smsJerryChannelService.sendMsg(smsMessageTask);
                        }else if (device.getDeviceChannel().equals("2")) {
                            thirdId = smsCatChannelService.sendMsg(smsMessageTask);
                        }
                        if (thirdId != null) {
                            smsMessageTaskMapper.success(smsMessageTask.getId());
                            addHandleTask(smsMessageTask.getUserName());
                            deviceMapper.success(smsMessageTask.getMessageDeviceCode());
                            deviceMapper.updateLastTaskId(device.getId(), smsMessageTask.getId());
                        } else {
                            smsMessageTaskMapper.failed(smsMessageTask.getId());
                            recoveryBalance(smsMessageTask.getUserName());
                            deviceMapper.failed(smsMessageTask.getMessageDeviceCode());
                            log.info(String.format("退还费用[%s]", smsMessageTask.getUserName()));
                            this.recoveryBalance(smsMessageTask.getUserName());
                            redisUtil.incr(smsMessageTask.getMessageDeviceCode(), 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("执行SendMsgJob 失败", e);
                    } finally {
                        deviceMapper.updateLastHandleTime(smsMessageTask.getMessageDeviceCode());
                        smsMessageTask = smsMessageTaskMapper.getWaitTaskByUserName(device.getBindUser());
                        if (smsMessageTask == null) {
                            telegramBot.sendToChats(String.format("用户 [%s] 任务已完成", device.getBindUser()));
                            chatService.systemSend(device.getBindUser(), "任务已完成");
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            if (CollectionUtils.isEmpty(deviceList)) {
                log.info("暂无可执行任务");
            } else {
                log.info(String.format(" Jeecg-Boot 发送消息任务 SendMsgJob !  时间:" + DateUtils.getTimestamp()));
            }
        }

    }

    private void addHandleTask(String userName) {
        userService.addHandleTask(userName);
    }

    private void recoveryBalance(String username) {
        userService.recoveryBalance(username);
    }

}
