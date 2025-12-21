package org.jeecg.modules.quartz.job;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.airag.app.entity.request.MCPortStatusRequest;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.sms.entity.bo.SmsCodeMatchBO;
import org.jeecg.modules.sms.entity.bo.SmsStockBO;
import org.jeecg.modules.sms.mapper.SmsRentMapper;
import org.jeecg.modules.sms.mapper.SmsTemplateMapper;
import org.jeecg.modules.sms.utils.NumberToExcelColumn;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
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
public class AsyncJob2 implements Job {

    @Autowired
    private SmsRentMapper smsRentMapper;
    @Autowired
    private SmsTemplateMapper smsTemplateMapper;
    @Autowired
    private SmsDeviceMapper smsDeviceMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private TelegramBot.MyTelegramBot telegramBot;
    private static ReentrantLock lock = new ReentrantLock();
    private static HashMap<String,String> map = new HashMap();
    private static HashMap<String,Integer> nextIdMap = new HashMap();
    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        log.info(" --- 同步任务调度开始 --- ");
        boolean getLock = lock.tryLock();
        if (getLock){
        try {
            List<SmsRent> block = smsRentMapper.toBlock();
            if (!CollectionUtil.isEmpty(block)){
                block.forEach(e->{
                    sysUserMapper.recoveryBalanceBySmsPrice(e.getUserName(),e.getPrice());
                    //转超时
                    smsRentMapper.updateToBlack(e.getRentId());
                    SysUser user = sysUserMapper.getUserByName(e.getUserName());
                    telegramBot.sendToChats(String.format("[用户:%s] 系统自动回收号码 项目:[%s] 单价[%s] 余额[%s]", e.getUserName(),e.getProjectCode(),e.getPrice().toPlainString(),user.getBalance().toPlainString()));
                });
            }
            List<SmsRent> block2 = smsRentMapper.toBlock2();
            if (CollectionUtil.isEmpty(block2)){
                block2.forEach(e->{
                    smsRentMapper.blackToDel(e.getRentId());
                });
            }
            smsRentMapper.toBlock3();
            String uuid = UUID.randomUUID().toString();
            Integer wakeUp = smsRentMapper.toWakeUp(uuid);
            if (wakeUp>=1){
                SmsRent byApplyCode = smsRentMapper.getByApplyCode(uuid);
                SmsDevice device = smsDeviceMapper.selectById(byApplyCode.getDeviceId());
                log.info("等待唤醒-激活卡槽:{} {} ",byApplyCode.getDevicePort(),byApplyCode.getSlotNum());
                HttpUtils.doGet(device.getDeviceOtherInfo().replace("goip_get_sms","goip_send_cmd")+"&op=switch&port="+device.getDeviceId()+ NumberToExcelColumn.numberToColumn(Integer.parseInt(device.getSlotNum())));
            }
            log.info("更新余额");
        }catch (Exception e){
            e.printStackTrace();
            log.info("更新余额错误");
        }finally {
            lock.unlock();
        }
        }

        //测试发现 每5秒执行一次
//        log.info(" --- 执行完毕，时间："+DateUtils.now()+"---");
    }



}
