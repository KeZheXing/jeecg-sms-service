package org.jeecg.modules.code.entity.request;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class CodeListRequest {

    Integer pageNo;
    Integer pageSize;
    Integer codeStatus;
    String phone;
}
