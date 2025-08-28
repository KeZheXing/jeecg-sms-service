package org.jeecg.modules.message.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.dto.message.MessageDTO;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.message.entity.SysMessage;
import org.jeecg.modules.message.handle.enums.SendMsgStatusEnum;
import org.jeecg.modules.message.service.ISysMessageService;
import org.jeecg.modules.sms.entity.SmsMessageTask;
import org.jeecg.modules.sms.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.sms.service.ISmsChannelService;
import org.jeecg.modules.system.service.ISysUserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发送消息任务
 * @author: jeecg-boot
 */

@Slf4j
public class SendMsgJob implements Job {

	@Autowired
	private SmsMessageTaskMapper smsMessageTaskMapper;

    @Autowired
    private ISmsChannelService iSmsChannelService;

    @Autowired
    private ISysUserService userService;

	@Override
    @Transactional
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<SmsMessageTask> waitTask = smsMessageTaskMapper.getWaitTask();
        waitTask.forEach(smsMessageTask -> {
            reduceCost(smsMessageTask);
            Boolean result = iSmsChannelService.sendMsg(smsMessageTask);
            if (result){
                smsMessageTaskMapper.success(smsMessageTask.getId());
            }
        });
        log.info(String.format(" Jeecg-Boot 发送消息任务 SendMsgJob !  时间:" + DateUtils.getTimestamp()));


	}

    private void reduceCost(SmsMessageTask smsMessageTask) {
        userService.reduceSendCost(smsMessageTask.getUserName());
    }

}
