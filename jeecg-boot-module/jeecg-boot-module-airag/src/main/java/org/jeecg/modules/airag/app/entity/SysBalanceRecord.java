package org.jeecg.modules.airag.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysBalanceRecord implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private  Integer recordId; // 模板ID
    private String userName;
    private BigDecimal balance;
    private BigDecimal changeBalance;
    private LocalDateTime createdTime;
    private String createdBy;
    private LocalDate recordDay;
    private BigDecimal waitBalance;
    private String hashCode;
    private String recordStatus;
}
