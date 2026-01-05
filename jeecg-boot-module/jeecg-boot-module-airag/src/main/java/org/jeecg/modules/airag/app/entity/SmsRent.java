package org.jeecg.modules.airag.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmsRent implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private  Integer rentId; // 模板ID
    private String phone;
    private String userName;
    private String projectCode;
    @TableField(exist = false)
    private String projectName;
    private Integer rentStatus;
    private Integer rentType;
    private String content;
    private String code;
    private BigDecimal price;
    private LocalDateTime createdTime;
    private LocalDateTime receiveTime;
    private String applyCode;
    @TableField(exist = false)
    private String countdown="-";
    private LocalDateTime expiredTime;
    private String deviceId;
    @TableField(exist = false)
    private String apiToken="-";
    private String slotNum;
    private String devicePort;
    private LocalDateTime wakeupExpireTime;
    @TableField(exist = false)
    private Integer num;
}
