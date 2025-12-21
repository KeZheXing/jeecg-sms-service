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
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmsPrice implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private  Integer id; // 模板ID
    private String userName;
    private String templateCode;
    private Boolean priceStatus;
    private LocalDateTime createdTime;
    private BigDecimal price;
    private Integer quota;
}
