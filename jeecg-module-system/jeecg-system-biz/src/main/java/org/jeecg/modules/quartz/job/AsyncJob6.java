package org.jeecg.modules.quartz.job;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.quartz.entity.ListenerRecord;
import org.jeecg.modules.sms.utils.NumberToExcelColumn;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
public class AsyncJob6 implements Job {


    private static HashMap<String,String> map = new HashMap();
    private static HashMap<String,Integer> nextIdMap = new HashMap();

    @Autowired
    private SmsDeviceMapper smsDeviceMapper;
    @Autowired
    private TelegramBot.MyTelegramBot myTelegramBot;


    final static ReentrantLock reentrantLock = new ReentrantLock();
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        boolean lock = reentrantLock.tryLock();
        if (lock){
            try {
                String dictValue = smsDeviceMapper.getDictValue("系统参数", "设备状态回传告警时间");
                List<SmsDevice> smsDevices = smsDeviceMapper.noticeWarn(dictValue);
                smsDevices.stream().filter(e->Boolean.TRUE.equals(e.getNeedActive())).forEach(e->{
                    myTelegramBot.sendToChats(String.format("[WARN!!!][WARN!!!][WARN!!!] 设备[%s] 状态通知疑似关闭!!!!!!!", e.getDeviceUserName()));
                });
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                reentrantLock.unlock();
            }
        }

    }

    private void handle() {

    }

    @Data
public static class ListenerRecordDTO{
    /**
     * 监听id
     */
    private Integer recordId;

    /**
     * 项目id
     */
    private String transactionId;

    /**
     * 是否确认
     */
    private Boolean confirmed;

    private String contractRet;

    private String finalResult;

    /**
     * 事件类型
     */
    private String eventType;

    private String contractType;

    private String contractAddress;

    private String fromAddress;

    private String toAddress;

    private TokenInfo tokenInfo;

    private Integer matchStatus;

    private Long quant;

    public ListenerRecord toEntity() {
        ListenerRecord listenerRecord = new ListenerRecord();
        BeanUtils.copyProperties(this,listenerRecord);
        listenerRecord.setTokenAbbr(this.tokenInfo.getTokenAbbr());
        listenerRecord.setTokenDecimal(this.tokenInfo.getTokenDecimal());
        listenerRecord.setTokenName(this.tokenInfo.getTokenName());
        return listenerRecord;
    }

    @Data
    public static class TokenInfo{
        private String tokenId;

        private String tokenAbbr;

        private String tokenName;

        private Integer tokenDecimal;
    }
}


}
