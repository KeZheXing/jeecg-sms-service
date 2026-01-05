package org.jeecg.modules.sms.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.airag.app.entity.SmsRent;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface ISmsRentService extends IService<SmsRent> {

    Result<IPage<SmsRent>> queryPageList(HttpServletRequest req, QueryWrapper<SmsRent> queryWrapper, Integer pageSize, Integer pageNo);

    SmsRent apply(String projectCode);

    void blackNum(Integer rentId);

    void done(Integer rentId);

    void removeBlack(Integer rentId);

    void callbackMC(List<String> data, String username);

    Result<Boolean> addTime(SmsRent smsRent);

    Result<IPage<SmsRent>> queryBlackAllPageList(HttpServletRequest req, QueryWrapper<SmsRent> queryWrapper, Integer pageSize, Integer pageNo);

    Result<String> wakeup(Integer rentId);

    Result<String> applyApi(String projectCode, Integer num);
}
