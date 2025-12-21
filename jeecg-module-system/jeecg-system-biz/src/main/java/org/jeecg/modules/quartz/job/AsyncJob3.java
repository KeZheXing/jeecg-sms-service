package org.jeecg.modules.quartz.job;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.airag.app.entity.SysStatisticsCost;
import org.jeecg.modules.sms.mapper.SmsRentMapper;
import org.jeecg.modules.system.mapper.SysStatisticsCostMapper;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Description: 同步定时任务测试
 *
 * 此处的同步是指 当定时任务的执行时间大于任务的时间间隔时
 * 会等待第一个任务执行完成才会走第二个任务
 *
 *
 * @author: taoyan
 * @date: 2020年06月19日
 */
//@PersistJobDataAfterExecution
//@DisallowConcurrentExecution
@Slf4j
@Component
public class AsyncJob3 implements Job {

    @Autowired
    private  SmsRentMapper smsRentMapper;
    @Autowired
    private SysStatisticsCostMapper sysStatisticsCostMapper;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 小时（24小时制）
        calendar.set(Calendar.MINUTE, 0);      // 分钟
        calendar.set(Calendar.SECOND, 0);      // 秒
        calendar.set(Calendar.MILLISECOND, 0); // 毫秒
        Date todayStart = calendar.getTime();

        // 2. 获取今天 24:00:00（次日凌晨 00:00:00）
        calendar.add(Calendar.DAY_OF_MONTH, 1); // 日期加1天
        Date todayEnd = calendar.getTime();
        List<SysStatisticsCost> statisticsCost = smsRentMapper.getStatisticsCost(todayStart, todayEnd);
        if (!CollectionUtil.isEmpty(statisticsCost)) {
            statisticsCost.forEach(e->{
                sysStatisticsCostMapper.add(e);
            });
        }
        log.info("更新消费记录");

    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 小时（24小时制）
        calendar.set(Calendar.MINUTE, 0);      // 分钟
        calendar.set(Calendar.SECOND, 0);      // 秒
        calendar.set(Calendar.MILLISECOND, 0); // 毫秒
        Date todayStart = calendar.getTime();
        System.out.println("今天凌晨：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(todayStart));

        // 2. 获取今天 24:00:00（次日凌晨 00:00:00）
        calendar.add(Calendar.DAY_OF_MONTH, 1); // 日期加1天
        Date todayEnd = calendar.getTime();
        System.out.println("今天24点：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(todayEnd));

    }


}
