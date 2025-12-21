package org.jeecg.modules.quartz.job;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.request.MCPortStatusRequest;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.sms.entity.bo.SmsCodeMatchBO;
import org.jeecg.modules.sms.entity.bo.SmsStockBO;
import org.jeecg.modules.sms.mapper.SmsRentMapper;
import org.jeecg.modules.sms.mapper.SmsTemplateMapper;
import org.jeecg.modules.system.mapper.SysDictItemMapper;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class AsyncJob implements Job {

    @Autowired
    private SmsRentMapper smsRentMapper;
    @Autowired
    private SmsTemplateMapper smsTemplateMapper;
    @Autowired
    private SmsDeviceMapper smsDeviceMapper;
    private static HashMap<String,String> map = new HashMap();
    private static HashMap<String,Integer> nextIdMap = new HashMap();
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String uuid = UUID.randomUUID().toString();
        List<SmsStockBO> stock1 = smsRentMapper.getStock1();
        List<SmsStockBO> stock2 = smsRentMapper.getStock2();
        List<SmsStockBO> stock3 = smsRentMapper.getStock3();
        List<SmsStockBO> stock = new ArrayList<>();
        stock.addAll(stock1);
        stock.addAll(stock2);
        stock.addAll(stock3);
        if (stock!=null){
            stock.forEach(e->{
                smsTemplateMapper.updateStock(e.getTemplateCode(),e.getCount(),uuid);
            });
        }
        smsDeviceMapper.clearStock(uuid);
        smsRentMapper.clearCode();
//        getCode();
//        getStatus();
    }

}
