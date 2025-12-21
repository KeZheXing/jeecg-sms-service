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
public class SmsProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     * 项目ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 项目编码-唯一编码
     */
    private String projectCode;

    /**
     * 模板
     */
    private String template;



}
