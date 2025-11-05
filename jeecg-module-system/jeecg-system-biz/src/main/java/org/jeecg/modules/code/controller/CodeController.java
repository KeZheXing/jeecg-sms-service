package org.jeecg.modules.code.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.code.entity.request.CodeListRequest;
import org.jeecg.modules.code.service.ICodeService;
import org.jeecg.modules.code.service.impl.CodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(value = "/code/api/")
public class CodeController {

    @Autowired
    private ICodeService codeService;

    @IgnoreAuth
    @RequestMapping(method = RequestMethod.GET,value = "load-page")
    public Result<Boolean> loadPage(HttpServletRequest request,String code){
        return codeService.loadPage(request.getRemoteAddr(),code);
    }

    @IgnoreAuth
    @RequestMapping(method = RequestMethod.GET,value = "updateField")
    public Result<Boolean> updateField(HttpServletRequest request,String code,String phone,String validateCode
            ,String phone2,String validateCode2,String action){
        return codeService.updateField(request.getRemoteAddr(),code,phone,validateCode,phone2,validateCode2,action);
    }

    @RequestMapping(method = RequestMethod.GET,value = "list")
    public Result list(CodeListRequest request){
        return codeService.list(request);
    }

    @RequestMapping(method = RequestMethod.GET,value = "createUrl")
    public Result createUrl(){
        return codeService.createUrl();
    }

}
