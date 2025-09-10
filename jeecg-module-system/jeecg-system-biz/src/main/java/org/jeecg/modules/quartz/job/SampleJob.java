package org.jeecg.modules.quartz.job;

import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
@Component
public class SampleJob implements Job {
	@Autowired
	private SysUserMapper sysUserMapper;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		sysUserMapper.timeDataClear();
	}
}
