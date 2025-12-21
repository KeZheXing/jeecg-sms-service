package org.jeecg.modules.quartz.job;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.quartz.entity.ListenerRecord;
import org.jeecg.modules.sms.utils.NumberToExcelColumn;
import org.jeecg.modules.system.mapper.SysBalanceRecordMapper;
import org.jeecg.modules.system.service.ISysUserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class AsyncJob5 implements Job {


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
                List<SmsDevice> smsDevices = smsDeviceMapper.needUpdatePhoneList();
                if (smsDevices.isEmpty()){
                    return;
                }
                List<SmsDevice> collect = smsDevices.stream().collect(Collectors.groupingBy(SmsDevice::getDevicePort))
                        .values() // 拿到所有分组的List<Device>
                        .stream()
                        .map(list -> list.get(0)) // 每组取第一个
                        .collect(Collectors.toList());
                collect.forEach(smsDevice -> {
                    log.info("自动激活卡槽获取号码:{} {} ",smsDevice.getDevicePort(),smsDevice.getSlotNum());
                    HttpUtils.doGet(smsDevice.getDeviceOtherInfo().replace("goip_get_sms","goip_send_cmd")+"&op=switch&port="+smsDevice.getDeviceId()+ NumberToExcelColumn.numberToColumn(Integer.parseInt(smsDevice.getSlotNum())));
                    myTelegramBot.sendToChats(String.format("自动激活卡槽获取号码 设备端口[%s] 卡槽[%s]", smsDevice.getDevicePort() ,smsDevice.getSlotNum()));
                    smsDeviceMapper.updateNeedActive(smsDevice.getId());
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
