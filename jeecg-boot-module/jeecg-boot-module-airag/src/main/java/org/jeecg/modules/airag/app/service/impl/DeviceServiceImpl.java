package org.jeecg.modules.airag.app.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.chatgpt.service.AiChatService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.consts.IMConstants;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.service.IDeviceService;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.vo.SmsCallbackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
@Slf4j
public class DeviceServiceImpl extends ServiceImpl<SmsDeviceMapper, SmsDevice> implements IDeviceService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AiChatService chatService;

    @Override
    public Result<IPage<SmsDevice>> queryPageList(HttpServletRequest req, QueryWrapper<SmsDevice> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsDevice>> result = new Result<IPage<SmsDevice>>();

        //TODO 外部模拟登陆临时账号，列表不显示

        Page<SmsDevice> page = new Page<SmsDevice>(pageNo, pageSize);
        IPage<SmsDevice> pageList = this.page(page, queryWrapper);

        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

    @Override
    public Boolean debug(SmsDevice device) {
        try {
            IMConstants.calBackEvent.forEach(evet->{
                String query = "https://sms-online.top/api/3rdparty/v1/webhooks";
                String req = "{\n" +
                        "  \"id\": \""+device.getDeviceCode()+":"+evet+"\",\n" +

                        "  \"event\": \""+evet+"\",\n" +
                        "  \"url\": \"https://sms-online.top/callbackv1\"\n" +
                        "}";
                HttpUtils.doPostByAuth(query, req,device.getDeviceUserName(),device.getDevicePassword());
            });
        }catch (Exception e){
            e.printStackTrace();
            log.error("调试失败",e);
            return false;
        }
        return true;
    }

    @Override
    public Result<IPage<SmsDevice>> callback(SmsCallbackRequest smsCallbackRequest) {
        //幂等
        if (redisTemplate.opsForValue().increment(smsCallbackRequest.getId())>1){
            log.info("重复的消息");
        }
        if (IMConstants.SMS_RECEIVED.equals(smsCallbackRequest.getEvent())){

        }
        return null;
    }
}
