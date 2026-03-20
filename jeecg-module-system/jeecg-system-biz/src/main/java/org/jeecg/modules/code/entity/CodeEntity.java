package org.jeecg.modules.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("code")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CodeEntity   implements Serializable {

    private Integer codeId;

    private String codeStatus;

    /**
     * 标识
     */
    private String flag;

    private String code;

    private Integer finished;

    private String name;

    private String  phone;

    private String  phone2;

    private String  validateCode;

    private String  validateCode2;

    private String count;

    private String customerNum;



    /**
     * 时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 过期时间
     */
    private LocalDateTime updateTime;

    /**
     * json
     */
    private String codeJson;

}
