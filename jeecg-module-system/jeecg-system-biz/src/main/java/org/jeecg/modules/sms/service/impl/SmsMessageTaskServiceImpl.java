package org.jeecg.modules.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.AssertUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.sms.entity.SmsDevice;
import org.jeecg.modules.sms.entity.SmsMessageTask;
import org.jeecg.modules.sms.entity.vo.SmsMessageTaskAddVO;
import org.jeecg.modules.sms.mapper.SmsDeviceMapper;
import org.jeecg.modules.sms.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.sms.service.IDeviceService;
import org.jeecg.modules.sms.service.ISmsMessageTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
public class SmsMessageTaskServiceImpl extends ServiceImpl<SmsMessageTaskMapper, SmsMessageTask> implements ISmsMessageTaskService {

    @Autowired
    private SmsDeviceMapper smsDeviceMapper;

    @Override
    public Result<IPage<SmsMessageTask>> queryPageList(HttpServletRequest req, QueryWrapper<SmsMessageTask> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsMessageTask>> result = new Result<IPage<SmsMessageTask>>();

        //TODO 外部模拟登陆临时账号，列表不显示

        Page<SmsMessageTask> page = new Page<SmsMessageTask>(pageNo, pageSize);
        IPage<SmsMessageTask> pageList = this.page(page, queryWrapper);

        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

    @Override
    @Transactional
    public void add(SmsMessageTaskAddVO addMessageTask) {
        AssertUtils.assertNotEmpty("目标号码不能为空",addMessageTask.getTargetNums());
        AssertUtils.assertNotEmpty("内容不能为空",addMessageTask.getMessageContent());
        String[] split = addMessageTask.getTargetNums().split(",");
        String username = JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest());
        List<SmsDevice> devices = smsDeviceMapper.getByUserName(username);
        AssertUtils.assertTrue("设备为空", !CollectionUtils.isEmpty(devices));
        Integer count = 0 ;
        List<SmsMessageTask> list = new ArrayList<>();
        java.util.Date date = new java.util.Date();
        for (String num : split) {
            AssertUtils.assertTrue("号码格式异常",num.length()>10&&num.length()<=13);
            SmsMessageTask smsMessageTask = new SmsMessageTask();
            smsMessageTask.setMessageContent(addMessageTask.getMessageContent());
            smsMessageTask.setMessageTo(num);
            smsMessageTask.setMessageStatus("0");
            smsMessageTask.setMessageDeviceCode(devices.get(count % devices.size()).getDeviceCode());
            smsMessageTask.setCreatedTime(date);
            smsMessageTask.setUpdatedTime(date);
            smsMessageTask.setMessageType("1");
            smsMessageTask.setUserName(JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest()));
            list.add(smsMessageTask);
            count++;
        }
        this.saveBatch(list,500);
    }

}
