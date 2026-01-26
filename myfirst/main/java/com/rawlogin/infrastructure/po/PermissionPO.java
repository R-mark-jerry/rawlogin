package com.rawlogin.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * 权限持久化对象
 * 对应数据库中的 permissions 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("permissions")
public class PermissionPO {
    
    /**
     * 权限ID
     */
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer id;
    
    /**
     * 权限名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 权限代码
     */
    @TableField("code")
    private String code;
    
    /**
     * 权限描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 所属模块
     */
    @TableField("module")
    private String module;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}