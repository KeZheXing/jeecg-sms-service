package org.jeecg.modules.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.sms.entity.SmsDevice;
import org.jeecg.modules.sms.mapper.SmsDeviceMapper;
import org.jeecg.modules.sms.service.IDeviceService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
public class DeviceServiceImpl extends ServiceImpl<SmsDeviceMapper, SmsDevice> implements IDeviceService {
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
}
