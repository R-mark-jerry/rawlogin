package com.rawlogin.interfaces.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户视图对象
 * 用于前端展示，包含格式化和展示相关的字段
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    
    private Integer id;
    private String username;
    private String email;
    private String role;
    private String roleText; // 角色文本描述
    private Integer status;
    private String statusText; // 状态文本描述
    private String statusClass; // 状态CSS类名
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createTimeFormatted; // 格式化的创建时间
    private String updateTimeFormatted; // 格式化的更新时间
    private LocalDateTime lastLoginTime;
    private String lastLoginTimeFormatted; // 格式化的最后登录时间
    
    // 带参数构造函数
    public UserVO(Integer id, String username) {
        this.id = id;
        this.username = username;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
        // 自动设置角色文本
        this.roleText = "ADMIN".equals(role) ? "管理员" : "普通用户";
    }
    
    public String getRoleText() {
        return roleText;
    }
    
    public void setRoleText(String roleText) {
        this.roleText = roleText;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
        // 自动设置状态文本和CSS类
        if (status != null) {
            if (status == 1) {
                this.statusText = "启用";
                this.statusClass = "status-active";
            } else {
                this.statusText = "禁用";
                this.statusClass = "status-inactive";
            }
        }
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public String getStatusClass() {
        return statusClass;
    }
    
    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        // 自动格式化时间
        if (createTime != null) {
            this.createTimeFormatted = createTime.toString();
        }
    }
    
    public String getCreateTimeFormatted() {
        return createTimeFormatted;
    }
    
    public void setCreateTimeFormatted(String createTimeFormatted) {
        this.createTimeFormatted = createTimeFormatted;
    }
    
    public String getUpdateTimeFormatted() {
        return updateTimeFormatted;
    }
    
    public void setUpdateTimeFormatted(String updateTimeFormatted) {
        this.updateTimeFormatted = updateTimeFormatted;
    }
    
    public String getLastLoginTimeFormatted() {
        return lastLoginTimeFormatted;
    }
    
    public void setLastLoginTimeFormatted(String lastLoginTimeFormatted) {
        this.lastLoginTimeFormatted = lastLoginTimeFormatted;
    }
}