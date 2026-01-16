package org.jeecg.modules.sms.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.api.vo.ResultApi;
import org.jeecg.modules.airag.app.entity.SmsRent;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:33
 **/
public interface ISmsRentApiService extends IService<SmsRent> {

    Result<IPage<SmsRent>> queryPageList(HttpServletRequest req, QueryWrapper<SmsRent> queryWrapper, Integer pageSize, Integer pageNo);

    ResultApi<JSONObject> apply(String projectCode, String apiToken);

    ResultApi blackNum(Long rentId, String apiToken);

    ResultApi done(Long rentId, String apiToken);

    void removeBlack(Long rentId);

    void callbackMC(List<String> data, String username);

    ResultApi getCode(Long rentId, String apiToken);

    Result<String> wakeup(Long rentId);

    ResultApi apiGetCode(String code);
}
