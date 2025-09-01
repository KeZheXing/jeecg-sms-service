package org.jeecg.modules.airag.app.entity;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:35
 **/

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmsDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     * 设备ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 账号
     */
    private String deviceUserName;

    /**
     * 密码
     */
    private String devicePassword;

    /**
     * 间隔
     */
    @TableField(value = "`interval`")
    private Integer interval;

    /**
     * 发送上限
     */
    private Integer sendLimit;

    /**
     * 设备状态
     */
    private String deviceStatus;

    private Integer taskNum;

    private Integer send;

    private Integer receive;

    private Integer reply;

    private String bindUser;

}
