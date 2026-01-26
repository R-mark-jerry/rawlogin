package com.rawlogin.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 角色持久化对象 (PO)
 * 用于数据库映射和持久化操作
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("roles")
public class RolePO {
    
    /**
     * 角色ID
     */
    @TableId
    private Integer id;
    
    /**
     * 角色名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 角色代码
     */
    @TableField("code")
    private String code;
    
    /**
     * 角色描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 状态：1-启用，0-禁用
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private String updateTime;
}