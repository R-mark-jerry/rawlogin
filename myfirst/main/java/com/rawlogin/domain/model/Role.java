package com.rawlogin.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 角色领域模型
 * 表示业务领域中的角色概念
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    
    /**
     * 角色ID
     */
    private Integer id;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色代码
     */
    private String code;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 角色权限列表
     */
    private List<String> permissions;
    
    /**
     * 角色用户数量
     */
    private Integer userCount;
    
    /**
     * 是否为内置角色
     */
    private Boolean builtIn;
    
    /**
     * 检查角色是否启用
     * @return 是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
    
    /**
     * 检查是否为内置角色
     * @return 是否为内置角色
     */
    public boolean isBuiltIn() {
        return builtIn != null && builtIn;
    }
    
    /**
     * 检查是否拥有指定权限
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    public boolean hasPermission(String permissionCode) {
        if (permissions == null || permissionCode == null) {
            return false;
        }
        return permissions.contains(permissionCode);
    }
    
    /**
     * 添加权限
     * @param permissionCode 权限代码
     */
    public void addPermission(String permissionCode) {
        if (permissions == null) {
            permissions = new java.util.ArrayList<>();
        }
        if (!permissions.contains(permissionCode)) {
            permissions.add(permissionCode);
        }
    }
    
    /**
     * 移除权限
     * @param permissionCode 权限代码
     */
    public void removePermission(String permissionCode) {
        if (permissions != null) {
            permissions.remove(permissionCode);
        }
    }
    
    /**
     * 清空所有权限
     */
    public void clearPermissions() {
        if (permissions != null) {
            permissions.clear();
        }
    }
    
    /**
     * 启用角色
     */
    public void enable() {
        this.status = 1;
    }
    
    /**
     * 禁用角色
     */
    public void disable() {
        this.status = 0;
    }
    
    /**
     * 获取角色类型
     * @return 角色类型
     */
    public String getType() {
        if (code == null) {
            return "UNKNOWN";
        }
        
        switch (code) {
            case "ADMIN":
                return "SYSTEM_ADMIN";
            case "USER":
                return "SYSTEM_USER";
            default:
                return "CUSTOM";
        }
    }
    
    /**
     * 检查是否为系统管理员角色
     * @return 是否为系统管理员
     */
    public boolean isSystemAdmin() {
        return "ADMIN".equals(code);
    }
    
    /**
     * 检查是否为普通用户角色
     * @return 是否为普通用户
     */
    public boolean isSystemUser() {
        return "USER".equals(code);
    }
    
    /**
     * 检查是否为自定义角色
     * @return 是否为自定义角色
     */
    public boolean isCustom() {
        return !isSystemAdmin() && !isSystemUser();
    }
    
    /**
     * 创建新角色实例
     * @param name 角色名称
     * @param code 角色代码
     * @param description 角色描述
     * @return 角色实例
     */
    public static Role create(String name, String code, String description) {
        Role role = new Role();
        role.setName(name);
        role.setCode(code);
        role.setDescription(description);
        role.setStatus(1); // 默认启用
        role.setBuiltIn(false); // 默认非内置
        role.setPermissions(new java.util.ArrayList<>());
        role.setUserCount(0);
        
        return role;
    }
    
    /**
     * 创建内置角色实例
     * @param name 角色名称
     * @param code 角色代码
     * @param description 角色描述
     * @param permissions 权限列表
     * @return 内置角色实例
     */
    public static Role createBuiltIn(String name, String code, String description, List<String> permissions) {
        Role role = create(name, code, description);
        role.setBuiltIn(true);
        role.setPermissions(permissions != null ? new java.util.ArrayList<>(permissions) : new java.util.ArrayList<>());
        
        return role;
    }
}