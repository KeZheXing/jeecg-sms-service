package org.jeecg.modules.message.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.airag.app.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.airag.app.service.ISmsChannelService;
import org.jeecg.modules.message.service.IJobService;
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
    private IJobService jobService;

	@Override

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        jobService.sendMsgJob();
	}

}
