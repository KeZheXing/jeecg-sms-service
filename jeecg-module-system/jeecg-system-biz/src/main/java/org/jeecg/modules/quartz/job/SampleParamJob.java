package org.jeecg.modules.quartz.job;

import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 示例带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
@Component
public class SampleParamJob implements Job {

	/**
	 * 若参数变量名修改 QuartzJobController中也需对应修改
	 */
	private String parameter;

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

	}
}
