package org.jeecg.modules.airag.app.entity;

/**
 * @Author: KKKKK
 * @Date: 2025/8/25 19:35
 **/

import com.baomidou.mybatisplus.annotation.IdType;
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
public class SmsMessageTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 设备编号
     */
    private String messageDeviceCode;

    /**
     * 账号
     */
    private String messageTo;

    /**
     * 密码
     */
    private String messageContent;

    /**
     * 间隔
     */
    private String messageType;

    /**
     * 发送上限
     */
    private String messageStatus;

    private String userName;

    private java.util.Date createdTime;

    private java.util.Date updatedTime;

    private java.util.Date handleTime;

}
