package org.jeecg.modules.sms.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.api.vo.ResultApi;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SmsPrice;
import org.jeecg.modules.airag.app.entity.SmsRent;
import org.jeecg.modules.airag.app.entity.SmsTemplate;
import org.jeecg.modules.airag.app.mapper.SmsDeviceMapper;
import org.jeecg.modules.airag.app.utils.HttpUtils;
import org.jeecg.modules.airag.app.utils.TelegramBot;
import org.jeecg.modules.sms.entity.bo.SmsCodeMatchBO;
import org.jeecg.modules.sms.mapper.SmsPriceMapper;
import org.jeecg.modules.sms.mapper.SmsRentMapper;
import org.jeecg.modules.sms.mapper.SmsTemplateMapper;
import org.jeecg.modules.sms.service.ISmsRentApiService;
import org.jeecg.modules.sms.utils.NumberToExcelColumn;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:34
 **/
@Service
@Slf4j
public class SmsRentApiServiceImpl extends ServiceImpl<SmsRentMapper, SmsRent> implements ISmsRentApiService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SmsTemplateMapper smsTemplateMapper;
    @Autowired
    private SmsDeviceMapper smsDeviceMapper;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SmsPriceMapper smsPriceMapper;
    @Autowired
    private TelegramBot.MyTelegramBot telegramBot;

    @Override
    public Result<IPage<SmsRent>> queryPageList(HttpServletRequest req, QueryWrapper<SmsRent> queryWrapper, Integer pageSize, Integer pageNo) {
        Result<IPage<SmsRent>> result = new Result<IPage<SmsRent>>();
        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ge("expired_time", LocalDateTime.now());
        Page<SmsRent> page = new Page<SmsRent>(pageNo, pageSize);
        IPage<SmsRent> pageList = this.page(page, queryWrapper);
        List<SmsRent> records = pageList.getRecords();
        if (records.size() > 0) {
            Set<String> collect = records.stream().map(SmsRent::getProjectCode).collect(Collectors.toSet());
            LambdaQueryWrapper<SmsTemplate> templateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            templateLambdaQueryWrapper.in(SmsTemplate::getTemplateCode,collect);
            Map<String, String> map = smsTemplateMapper.selectList(templateLambdaQueryWrapper).stream().collect(Collectors.toMap(SmsTemplate::getTemplateCode, SmsTemplate::getTemplateName));
            records.forEach(e->{
                e.setProjectName(map.get(e.getProjectCode())+"("+e.getProjectCode()+")");
            });
        }
        result.setSuccess(true);
        result.setResult(pageList);
        //log.info(pageList.toString());
        return result;
    }

    @Override
    @Transactional
    public ResultApi<JSONObject> apply(String projectCode, String apiToken) {
        if (StringUtils.isBlank(projectCode)|| "请选择".equals(projectCode)){
            return ResultApi.error("项目编码无效");
        }
        String username = checkApiToken(apiToken);
        //查询项目
        SmsTemplate template = smsTemplateMapper.getByCode(projectCode);
        if (template==null){
            return null;
        }
        SmsPrice smsPrice = smsPriceMapper.getPriceByUserNameAndProjectCode(username,template.getTemplateCode());
        if (this.baseMapper.waitCount(username,template.getTemplateCode())>= Optional.ofNullable(smsPrice).map(SmsPrice::getQuota).orElse(20)){
            throw new RuntimeException("当前用户配置的并发额度不足,请联系管理员提升配额");
        }
        Boolean reduceResult = userService.reduceBySmsPrice(username,smsPrice==null? template.getPrice():smsPrice.getPrice());
        if (!reduceResult){
            ResultApi<JSONObject> error = ResultApi.error("余额不足");
            error.setStatus("insufficient_balance");
            return error;
        }
        String applyCode = UUID.randomUUID().toString();
        String queryRent = String.valueOf(template.getRentType());
        if (template.getOnlyShort()){
            queryRent = "0";
        }else if (template.getRentType().equals(0)){
            queryRent = "0,1";
        }else {
            queryRent = "1";
        }
        Integer effect = this.baseMapper.apply(projectCode,applyCode,queryRent,username);
        if (effect==null||effect==0){
            throw new RuntimeException("暂无资源");
        }
        SmsDevice device = this.smsDeviceMapper.getByApplyCode(applyCode);
        SmsRent smsRent = new SmsRent();
        smsRent.setPrice(smsPrice==null?template.getPrice():smsPrice.getPrice());
        smsRent.setPhone(device.getPhone());
        smsRent.setCreatedTime(LocalDateTime.now());
        smsRent.setUserName(username);
        smsRent.setRentType(template.getRentType());
        smsRent.setProjectCode(template.getTemplateCode());
        smsRent.setApplyCode(applyCode);
        smsRent.setDeviceId(String.valueOf(device.getId()));
        smsRent.setSlotNum(device.getSlotNum());
        smsRent.setDevicePort(device.getDevicePort());
        if (smsRent.getRentType().equals(1)){
            smsRent.setExpiredTime(LocalDateTime.now().plusDays(30));
        }else {
            smsRent.setExpiredTime(LocalDateTime.now().plusMinutes(20));
        }
        int insert = this.baseMapper.insert(smsRent);
        if (insert==0){
            throw new RuntimeException("资源不可用");
        }
        if (device.getSlotStatus().equals("0/1/1")){
            log.info("激活卡槽:{} {} ",device.getDevicePort(),device.getSlotNum());
            HttpUtils.doGet(device.getDeviceOtherInfo().replace("goip_get_sms","goip_send_cmd")+"&op=switch&port="+device.getDeviceId()+ NumberToExcelColumn.numberToColumn(Integer.parseInt(device.getSlotNum())));
        }
        this.smsDeviceMapper.clearApplyCode(applyCode);
        SmsRent byApplyCode = this.baseMapper.getByApplyCode(applyCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rentId",byApplyCode.getRentId());
        jsonObject.put("phone",byApplyCode.getPhone());
        ResultApi<JSONObject> resultApi = ResultApi.OK(jsonObject);
        resultApi.setMessage("申请成功");
        resultApi.setStatus("active");
        SysUser user = userService.getUserByName(username);
        telegramBot.sendToChats(String.format("[用户:%s] API申请号码 项目:[%s] 单价[%s] 余额[%s]", username,smsRent.getProjectCode(),smsRent.getPrice().toPlainString(),user.getBalance().toPlainString()));
        return resultApi;
    }

    private String checkApiToken(String apiToken) {
        if (org.apache.commons.lang3.StringUtils.isBlank(apiToken)){
            throw new RuntimeException("apiToken not null");
        }
        SysUser sysUser = userService.getUserByApiToken(apiToken);
        return sysUser.getUsername();
    }

    @Override
    @Transactional
    public ResultApi blackNum(Integer rendId, String apiToken) {
        String username = checkApiToken(apiToken);
        Integer effect = this.baseMapper.blackNum(rendId, username);
        if (effect!=1){
            ResultApi<Object> error = ResultApi.error("拉黑失败");
            error.setStatus("not_allowed");
            return error;
        }else {
            SmsRent smsRent = this.baseMapper.getByRentId(rendId);
            userService.recoveryBalanceBySmsPrice(smsRent.getUserName(),smsRent.getPrice());
            SysUser user = userService.getUserByName(username);
            telegramBot.sendToChats(String.format("[用户:%s] API拉黑号码 项目:[%s] 单价[%s] 余额[%s]", username,smsRent.getProjectCode(),smsRent.getPrice().toPlainString(),user.getBalance().toPlainString()));
            ResultApi<Object> ok = ResultApi.ok();
            ok.setStatus("blacklisted");
            return ok;
        }
    }

    @Override
    public ResultApi done(Integer rentId, String apiToken) {
        String username = checkApiToken(apiToken);
        Integer effect = this.baseMapper.done(rentId, username);
        if (effect!=1){
            ResultApi<Object> error = ResultApi.error("完成失败");
            return error;
        }else {
            ResultApi<Object> ok = ResultApi.ok();
            ok.setStatus("done");
            return ok;
        }
    }

    @Override
    public void removeBlack(Integer rentId) {
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        Integer effect = this.baseMapper.removeBlack(rentId, username);
        if (effect!=1){
            throw new RuntimeException("移除失败");
        }
        
    }

    @Override
    public void callbackMC(List<String> data, String username) {
        if ("0".equals(data.get(0))){

        }
        String[] split = data.get(1).split("\\.");
        String port = split[0];
        String slot = split[1];
        String content = new String(Base64.getDecoder().decode(data.get(5).getBytes())).split("\n")[6].replace("-","");
        log.info("收到消息 {} {} {}",port,slot,content,username);
        List<SmsCodeMatchBO> info = this.baseMapper.getRentInfoByWait(port,username);
        if (updatePhone(username,port,slot,content)){
            return;
        }
        if (!CollectionUtil.isEmpty(info)){
            info.forEach(e->{
                if (countDifferentCharacters(e.getTemplateContent(),content)==e.getCaptchaLength()){
                    String difference = content.substring(e.getTemplateContent().indexOf(e.getPlaceholder()),e.getCaptchaLength());
                    if (difference.length()==e.getCaptchaLength()){
                        this.baseMapper.updateCode(e.getRentId(),difference,content);
                    }
                }
            });
        }

    }

    @Override
    public ResultApi getCode(Integer rentId, String apiToken) {
        String username = checkApiToken(apiToken);
        SmsRent rent = this.baseMapper.getByRentIdAndUserName(rentId, username);
        if (rent==null){
            ResultApi<Object> error = ResultApi.error("未找到该租赁 ID");
            error.setStatus("not_found");
            return error;
        }
        if (rent.getCode()==null){
            ResultApi error = ResultApi.error("验证码暂无");
            error.setStatus("pending");
            return error;
        }else {
            ResultApi ok = ResultApi.ok("验证码已到达");
            ok.setStatus("received");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",rent.getCode());
            ok.setResult(jsonObject);
            return ok;
        }
    }


    @Override
    public Result<String> wakeup(Integer rentId) {
        Integer effect = this.baseMapper.wakeUp(rentId);
        if (effect==1){
            SmsRent smsRent = this.baseMapper.selectById(rentId);
            SmsDevice device = smsDeviceMapper.selectById(smsRent.getDeviceId());
            if (device.getSlotStatus().equals("0/1/1")){
                log.info("唤醒激活卡槽:{} {} ",smsRent.getDevicePort(),smsRent.getSlotNum());
                HttpUtils.doGet(device.getDeviceOtherInfo().replace("goip_get_sms","goip_send_cmd")+"&op=switch&port="+device.getDeviceId()+ NumberToExcelColumn.numberToColumn(Integer.parseInt(device.getSlotNum())));
                return Result.ok("唤醒成功,激活中..");
            }else {
                return Result.ok("唤醒成功");
            }
        }else {
            SmsRent smsRent = this.baseMapper.selectById(rentId);
            SmsDevice device = smsDeviceMapper.selectById(smsRent.getDeviceId());
            SmsRent busySmsRent = this.baseMapper.getBusyNum(device.getDevicePort());
            return Result.error("端口繁忙..预计空闲时间"+busySmsRent.getExpiredTime());
        }

    }


    private boolean updatePhone(String username, String port,String slot, String content) {
        if (content.startsWith("TATE?state=NewAccount;server")){
            smsDeviceMapper.updatePhone(username,port,extractDigitsByLength(content,11), slot);
            return true;
        }else if (content.startsWith("Welcome to Vodafone! Your new number is")){
            smsDeviceMapper.updatePhone(username,port,"61"+extractDigitsByLength(content,10).replaceFirst("0",""), slot);
            return true;
        }
        return false;
    }

    private void addDefaultSmsTemplate(List<SmsCodeMatchBO> info, String content) {
        if (content.startsWith("TATE?state=NewAccount;server=")){

        }
        SmsCodeMatchBO smsCodeMatchBO = new SmsCodeMatchBO();
        smsCodeMatchBO.setTemplateCode("num");
//        smsCodeMatchBO.setCaptchaLength();
    }

    public static void main(String[] args) {
        System.out.println(extractDigitsByLength("TATE?state=NewAccount;server=mb2.messagebank.telstra.com;port=143;name=61475691719;pw=92q0mP61IRFHTAr4",11));
    }

    /**
     * 从文本中提取所有指定长度的连续数字
     * @param text 原始文本
     * @param length 需要提取的数字长度（正整数）
     * @return 符合长度的所有数字列表，无匹配则返回空列表
     * @throws IllegalArgumentException 如果长度为非正整数，抛出异常
     */
    public static String extractDigitsByLength(String text, int length) {
        // 校验入参合法性
        if (length <= 0) {
            throw new IllegalArgumentException("数字长度必须为正整数（length > 0）");
        }
        if (text == null || text.isEmpty()) {
            return null; // 空文本返回空列表
        }

        // 动态生成正则表达式：\d{length}（例如length=11时，表达式为\d{11}）
        String regex = "\\d{" + length + "}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 提取所有匹配的数字
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 从文本的键值对中提取指定key后的指定长度数字（如"name=xxx"中的xxx）
     * @param text 原始文本（包含类似key=value的键值对）
     * @param key 目标键（如"name"）
     * @param length 需要提取的数字长度（正整数）
     * @return 符合条件的数字，无匹配则返回null
     * @throws IllegalArgumentException 如果长度为非正整数，抛出异常
     */
    public static String extractDigitsAfterKey(String text, String key, int length) {
        // 校验入参合法性
        if (length <= 0) {
            throw new IllegalArgumentException("数字长度必须为正整数（length > 0）");
        }
        if (text == null || text.isEmpty() || key == null || key.isEmpty()) {
            return null; // 空文本或空key返回null
        }

        // 动态生成正则表达式：key=(\d{length})（例如key=name,length=11时，表达式为name=(\d{11})）
        String regex = key + "(=)(\\d{" + length + "})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 提取匹配的数字（捕获组2）
        if (matcher.find()) {
            return matcher.group(2); // group(2)对应数字部分
        }
        return null;
    }

    public static int countDifferentCharacters(String s1, String s2) {
        if (s1 == null || s2 == null) {
            // 处理null情况，这里假设null与任何字符串的所有字符都不同
            return Math.max(s1 == null ? 0 : s1.length(), s2 == null ? 0 : s2.length());
        }

        int minLength = Math.min(s1.length(), s2.length());
        int differenceCount = 0;

        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                differenceCount++;
            }
        }

        // 注意：此方法不计算长度差异带来的"额外"字符
        return differenceCount;
    }


}
