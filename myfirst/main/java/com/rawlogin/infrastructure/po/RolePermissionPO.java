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
 * 角色权限关联持久化对象
 * 对应数据库中的 role_permissions 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role_permissions")
public class RolePermissionPO {
    
    /**
     * 关联ID
     */
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer id;
    
    /**
     * 角色ID
     */
    @TableField("role_id")
    private Integer roleId;
    
    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Integer permissionId;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}