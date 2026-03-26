package org.jeecg.modules.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.code.entity.CodeEntity;
import org.jeecg.modules.code.entity.request.CodeListRequest;

public interface ICodeService  extends IService<CodeEntity> {
    Result<Boolean> loadPage(String remoteAddr,String code);

    Result list(CodeListRequest request);

    Result<Boolean> updateField(String remoteAddr, String code, String name, String phone, String validateCode, String phone2, String validateCode2, String action);

    Result createUrl();

    Result<Boolean> deleteById(Integer codeId);

}
