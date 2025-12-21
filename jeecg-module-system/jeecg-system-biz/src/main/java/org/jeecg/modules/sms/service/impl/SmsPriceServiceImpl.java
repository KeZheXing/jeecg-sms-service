package org.jeecg.modules.sms.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.SmsPrice;
import org.jeecg.modules.sms.mapper.SmsPriceMapper;
import org.jeecg.modules.sms.service.ISmsPriceService;
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
public class SmsPriceServiceImpl extends ServiceImpl<SmsPriceMapper, SmsPrice> implements ISmsPriceService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result<IPage<SmsPrice>> queryPageList(HttpServletRequest req, QueryWrapper<SmsPrice> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsPrice>> result = new Result<IPage<SmsPrice>>();

        //TODO 外部模拟登陆临时账号，列表不显示

        Page<SmsPrice> page = new Page<SmsPrice>(pageNo, pageSize);
        IPage<SmsPrice> pageList = this.page(page, queryWrapper);

        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

}
