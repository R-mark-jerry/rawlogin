package com.rawlogin.interfaces.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 角色视图对象 (VO)
 * 用于前端展示
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {
    
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
     * 状态文本
     */
    private String statusText;
    
    /**
     * 状态CSS类
     */
    private String statusClass;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 格式化的创建时间
     */
    private String createTimeFormatted;
    
    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 格式化的更新时间
     */
    private String updateTimeFormatted;
    
    /**
     * 角色权限列表
     */
    private List<PermissionVO> permissions;
    
    /**
     * 角色用户数量
     */
    private Integer userCount;
    
    /**
     * 是否为内置角色
     */
    private Boolean builtIn;
    
    /**
     * 是否可删除
     */
    private Boolean canDelete;
    
    /**
     * 是否可编辑
     */
    private Boolean canEdit;
    
    /**
     * 权限展示文本
     */
    private String permissionText;
    
    /**
     * 角色类型文本
     */
    private String typeText;
    
    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "启用" : "禁用";
    }
    
    /**
     * 获取状态CSS类
     */
    public String getStatusClass() {
        if (status == null) {
            return "status-unknown";
        }
        return status == 1 ? "status-enabled" : "status-disabled";
    }
    
    /**
     * 获取格式化的创建时间
     */
    public String getCreateTimeFormatted() {
        if (createTime == null || createTime.isEmpty()) {
            return "未知";
        }
        try {
            // 简单的时间格式化，实际项目中可能需要更复杂的处理
            return createTime.substring(0, 19).replace("T", " ");
        } catch (Exception e) {
            return createTime;
        }
    }
    
    /**
     * 获取格式化的更新时间
     */
    public String getUpdateTimeFormatted() {
        if (updateTime == null || updateTime.isEmpty()) {
            return "未知";
        }
        try {
            // 简单的时间格式化，实际项目中可能需要更复杂的处理
            return updateTime.substring(0, 19).replace("T", " ");
        } catch (Exception e) {
            return updateTime;
        }
    }
    
    /**
     * 获取权限展示文本
     */
    public String getPermissionText() {
        if (permissions == null || permissions.isEmpty()) {
            return "无权限";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < permissions.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(permissions.get(i).getName());
        }
        
        return sb.toString();
    }
    
    /**
     * 获取角色类型文本
     */
    public String getTypeText() {
        if (code == null) {
            return "未知类型";
        }
        
        switch (code) {
            case "ADMIN":
                return "系统管理员";
            case "USER":
                return "普通用户";
            default:
                return "自定义角色";
        }
    }
    
    /**
     * 权限视图对象内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionVO {
        private Integer id;
        private String name;
        private String code;
        private String description;
        private String category;
        private String displayName;
    }
}