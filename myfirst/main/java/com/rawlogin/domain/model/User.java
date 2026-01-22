package com.rawlogin.domain.model;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户领域模型
 * 包含业务逻辑和业务规则
 */
@TableName("users")
public class User {
    
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String realName;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    
    public User() {}
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // 业务规则：用户名长度验证
    public boolean isValidUsername() {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }
    
    // 业务规则：密码长度验证
    public boolean isValidPassword() {
        return password != null && password.length() >= 6;
    }
    
    // 业务规则：邮箱格式验证
    public boolean isValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            return true; // 邮箱可选
        }
        return email.contains("@");
    }
    
    // 业务规则：用户状态验证
    public boolean isActive() {
        return status != null && status == 1;
    }
    
    // 业务规则：是否为管理员
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    // 业务方法：设置默认值
    public void setDefaultsForNewUser() {
        this.createTime = LocalDateTime.now();
        this.status = 1; // 默认启用
        this.role = "USER"; // 默认普通用户
    }
    
    // 业务方法：清除敏感信息
    public void clearSensitiveInfo() {
        this.password = null;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}