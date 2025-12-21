package org.jeecg.modules.sms.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.SmsPrice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface ISmsPriceService extends IService<SmsPrice> {

    Result<IPage<SmsPrice>> queryPageList(HttpServletRequest req, QueryWrapper<SmsPrice> queryWrapper, Integer pageSize, Integer pageNo);

}
