package org.jeecg.modules.message.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.message.service.ICatCheckJobService;
import org.jeecg.modules.message.service.ICatReceiveService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送消息任务
 * @author: jeecg-boot
 */

@Slf4j
@Component
public class CatReceiveJob implements Job {


    @Autowired
    private ICatReceiveService catReceiveService;

	@Override

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        catReceiveService.reply();
	}

}
