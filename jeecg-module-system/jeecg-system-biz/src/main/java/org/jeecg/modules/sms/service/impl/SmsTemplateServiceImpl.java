package org.jeecg.modules.sms.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.chatgpt.service.AiChatService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.entity.SmsPrice;
import org.jeecg.modules.airag.app.entity.SmsTemplate;
import org.jeecg.modules.airag.app.entity.vo.SmsGetProjectList;
import org.jeecg.modules.sms.mapper.SmsPriceMapper;
import org.jeecg.modules.sms.mapper.SmsTemplateMapper;
import org.jeecg.modules.sms.service.ISmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
@Slf4j
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements ISmsTemplateService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AiChatService chatService;
    @Autowired
    private SmsPriceMapper smsPriceMapper;

    @Override
    public Result<IPage<SmsTemplate>> queryPageList(HttpServletRequest req, QueryWrapper<SmsTemplate> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsTemplate>> result = new Result<IPage<SmsTemplate>>();

        //TODO 外部模拟登陆临时账号，列表不显示

        Page<SmsTemplate> page = new Page<SmsTemplate>(pageNo, pageSize);
        IPage<SmsTemplate> pageList = this.page(page, queryWrapper);

        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

    @Override
    public Result<List<SmsGetProjectList>> getProjectList() {
        Result<IPage<SmsGetProjectList>> result = new Result<IPage<SmsGetProjectList>>();
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        LambdaQueryWrapper<SmsPrice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsPrice::getPriceStatus,1).eq(SmsPrice::getUserName,username);
        List<SmsPrice> smsPrices = smsPriceMapper.selectList(queryWrapper);
        LambdaQueryWrapper<SmsTemplate> smsTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        smsTemplateLambdaQueryWrapper.eq(SmsTemplate::getTemplateStatus,1);
        List<SmsTemplate> smsTemplates = this.baseMapper.selectList(smsTemplateLambdaQueryWrapper);
        List<SmsGetProjectList> list = new ArrayList<>();
        smsTemplates.forEach(e->{
            SmsGetProjectList smsGetProjectList = new SmsGetProjectList();
            smsGetProjectList.setProjectCode(e.getTemplateCode());
            smsGetProjectList.setProjectName("项目:"+e.getTemplateName()+"-库存:"+e.getStock()+"-价格:"+e.getPrice());
            list.add(smsGetProjectList);
        });
        Map<String, String> projectMap = smsTemplates.stream().collect(Collectors.toMap(SmsTemplate::getTemplateCode, SmsTemplate::getTemplateName));
        Map<String, Integer> stockMap = smsTemplates.stream().collect(Collectors.toMap(SmsTemplate::getTemplateCode, SmsTemplate::getStock));
        Set<String> enableProject = smsTemplates.stream().map(SmsTemplate::getTemplateCode).collect(Collectors.toSet());
        smsPrices.stream().filter(e->enableProject.contains(e.getTemplateCode())).forEach(e->{
            SmsGetProjectList smsGetProjectList = new SmsGetProjectList();
            smsGetProjectList.setProjectCode(e.getTemplateCode());
            smsGetProjectList.setProjectName("项目:"+projectMap.get(e.getTemplateCode())+"-库存:"+stockMap.get(e.getTemplateCode())+"-价格:"+e.getPrice());
            smsGetProjectList.setType("1");
            list.add(smsGetProjectList);
        });
        List<SmsGetProjectList> collect = list.stream() // 1. 按 code 分组
                .collect(Collectors.groupingBy(SmsGetProjectList::getProjectCode))
                // 2. 处理每个分组
                .values().stream()
                .map(group -> group.stream()
                        // 在组内优先查找 type == 1 的元素
                        .filter(product -> product.getType() != null)
                        // 如果找到了，取第一个；如果没找到，就取组内的第一个元素
                        .findFirst()
                        .orElseGet(() -> group.get(0))
                )
                // 3. 收集结果
                .collect(Collectors.toList());
        return Result.ok(collect);
    }

    public void updateStock(String templateCode){

    }

}
