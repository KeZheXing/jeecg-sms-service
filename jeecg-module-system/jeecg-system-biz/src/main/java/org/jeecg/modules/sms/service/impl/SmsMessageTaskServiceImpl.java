package org.jeecg.modules.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.AssertUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsMessageTask;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.mapper.SmsMessageTaskMapper;
import org.jeecg.modules.message.entity.SmsMessageTemplate;
import org.jeecg.modules.sms.entity.vo.SmsMessageTaskAddVO;
import org.jeecg.modules.sms.service.ISmsMessageTaskService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
@Slf4j
public class SmsMessageTaskServiceImpl extends ServiceImpl<SmsMessageTaskMapper, SmsMessageTask> implements ISmsMessageTaskService {

    @Autowired
    private SmsDeviceMapper smsDeviceMapper;

    @Autowired
    ISysUserService sysUserService;

    @Override
    public Result<IPage<SmsMessageTask>> queryPageList(HttpServletRequest req, QueryWrapper<SmsMessageTask> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsMessageTask>> result = new Result<IPage<SmsMessageTask>>();

        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.eq("username",JwtUtil.getUsername(TokenUtils.getTokenByRequest()));
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
        String[] split = addMessageTask.getTargetNums().split("\n");
        String username = JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest());
        List<SmsDevice> devices = smsDeviceMapper.getByUserName(username);
        AssertUtils.assertTrue("设备为空", !CollectionUtils.isEmpty(devices));
        Integer count = 0 ;
        List<SmsMessageTask> list = new ArrayList<>();
        java.util.Date date = new java.util.Date();
        AssertUtils.assertTrue("余额不足",sysUserService.reduceSendCost(username, split.length));
        sysUserService.addTask(username,split.length);
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

    @Override
    @Transactional
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<SmsMessageTemplate> clazz) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                // 获取上传文件对象
                MultipartFile file = entity.getValue();
                ImportParams params = new ImportParams();
//                params.setTitleRows(2);
//                params.setHeadRows(1);
//                params.setNeedSave(true);
                try {
                    List<SmsMessageTemplate> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                    //update-begin-author:taoyan date:20190528 for:批量插入数据
                    long start = System.currentTimeMillis();
                    addTask(list);
                    //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                    //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                    log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                    //update-end-author:taoyan date:20190528 for:批量插入数据
                    return Result.ok("文件导入成功！数据行数：" + list.size());
                } catch (Exception e) {
                    //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                    String msg = e.getMessage();
                    log.error(msg, e);
                    if(msg!=null && msg.indexOf("Duplicate entry")>=0){
                        return Result.error("文件导入失败:有重复数据！");
                    }else{
                        return Result.error("文件导入失败:" + e.getMessage());
                    }
                    //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
                } finally {
                    try {
                        file.getInputStream().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return Result.error("文件导入失败！");
    }

    private void addTask(List<SmsMessageTemplate> taskList) {
        String username = JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest());
        List<SmsDevice> devices = smsDeviceMapper.getByUserName(username);
        AssertUtils.assertTrue("设备为空", !CollectionUtils.isEmpty(devices));
        AtomicReference<Integer> count = new AtomicReference<>(0);
        List<SmsMessageTask> list = new ArrayList<>();
        java.util.Date date = new java.util.Date();
        AssertUtils.assertTrue("余额不足",sysUserService.reduceSendCost(username, taskList.size()));
        sysUserService.addTask(username,taskList.size());
        taskList.forEach(task->{
            AssertUtils.assertTrue("号码格式异常",task.getNum().length()>10&&task.getNum().length()<=13);
            SmsMessageTask smsMessageTask = new SmsMessageTask();
            smsMessageTask.setMessageContent(task.getContent());
            smsMessageTask.setMessageTo(task.getNum());
            smsMessageTask.setMessageStatus("0");
            smsMessageTask.setMessageDeviceCode(devices.get(count.get() % devices.size()).getDeviceCode());
            smsMessageTask.setCreatedTime(date);
            smsMessageTask.setUpdatedTime(date);
            smsMessageTask.setMessageType("1");
            smsMessageTask.setUserName(JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest()));
            list.add(smsMessageTask);
            count.getAndSet(count.get() + 1);
        });
        this.baseMapper.insert(list,200);
    }

}
