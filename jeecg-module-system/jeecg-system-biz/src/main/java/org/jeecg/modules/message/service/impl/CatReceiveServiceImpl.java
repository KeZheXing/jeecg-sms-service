package org.jeecg.modules.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.airag.app.consts.IMConstants;
import org.jeecg.modules.airag.app.entity.ConversationMessageRecords;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.mapper.ConversationMessageRecordsMapper;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.vo.SmsCallbackRequest;
import org.jeecg.modules.message.entity.CatReceiveResp;
import org.jeecg.modules.message.service.ICatReceiveService;
import org.jeecg.modules.message.service.IJobService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class CatReceiveServiceImpl implements ICatReceiveService {


    @Autowired
    private ISysUserService userService;
    @Autowired
    private ConversationMessageRecordsMapper conversationMessageRecordsMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private SmsDeviceMapper smsDeviceMapper;

    private final ReentrantLock lock = new ReentrantLock();
    @Transactional
    @Override
    public void reply() {
        boolean lockResult = lock.tryLock();
        if (!lockResult){
            return;
        }
        List<SmsDevice> deviceList = null;
        deviceList = smsDeviceMapper.getCatDevice();
        HashSet<String> portSet = new HashSet<>();
        deviceList.forEach(device->{
            String[] split = device.getDeviceOtherInfo().split(":");
            portSet.add(split[0]);
        });
        try {
            portSet.forEach(port->{
                handlePortData(port);
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            if (CollectionUtils.isEmpty(deviceList)) {
                log.info("暂无可接收任务");
            } else {
                log.info(String.format(" Jeecg-Boot 发送消息任务 SendMsgJob !  时间:" + DateUtils.getTimestamp()));
            }
        }

    }

    private void handlePortData(String port) {
        Object startId = redisTemplate.opsForValue().get(port+":startId");
        if (startId==null){
            redisTemplate.opsForValue().set(port+":startId","0");
            startId = 0;
        }
        String result = HttpUtils.doGet("http://sms-online.top:"+port+"/api.jsp?act=getSmsList&StartID=" + startId);
        List<CatReceiveResp> catReceiveResps = JSON.parseObject(result).getJSONArray("List").toJavaList(CatReceiveResp.class);
        if (CollectionUtils.isEmpty(catReceiveResps)){
            return;
        }
        catReceiveResps.stream().sorted(Comparator.comparing(data->Integer.parseInt(data.getID()))).forEach(data->{
            SmsDevice deviceCode = smsDeviceMapper.getByDeviceOtherInfo(port+":"+data.getPort());
            SmsCallbackRequest request = new SmsCallbackRequest();
            SmsCallbackRequest.Payload payload = new SmsCallbackRequest.Payload();
            payload.setMessage(data.getMessage());
            payload.setMessageId(data.getID()+data.getTime());
            payload.setPhoneNumber(data.getFrom());
            request.setPayload(payload);
            request.setEvent(IMConstants.SMS_RECEIVED);
            request.setId(data.getID()+data.getTime());
            request.setDeviceId(deviceCode.getDeviceCode());
            userService.callback(request);
            redisTemplate.opsForValue().set(port+":startId",data.getID());
        });
    }

    private void addHandleTask(String userName) {
        userService.addHandleTask(userName);
    }

    private void recoveryBalance(String username) {
        userService.recoveryBalance(username);
    }

}
