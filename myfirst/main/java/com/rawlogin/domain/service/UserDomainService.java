package com.rawlogin.domain.service;

import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.common.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户领域服务
 * 包含核心业务逻辑和业务规则
 */
@Service
public class UserDomainService {
    
    /**
     * 验证用户登录信息
     * @param userDTO 用户数据传输对象
     * @return 验证结果
     */
    public Result<String> validateLogin(String username, String password) {
        if (!isValidUsername(username)) {
            return Result.error("用户名格式不正确");
        }
        
        if (!isValidPassword(password)) {
            return Result.error("密码格式不正确");
        }
        
        return Result.success("验证通过");
    }
    
    /**
     * 验证用户注册信息
     * @param userDTO 用户数据传输对象
     * @return 验证结果
     */
    public Result<String> validateRegistration(String username, String password, String email) {
        if (!isValidUsername(username)) {
            return Result.error("用户名长度必须在3-50个字符之间");
        }
        
        if (!isValidPassword(password)) {
            return Result.error("密码长度不能少于6位");
        }
        
        if (!isValidEmail(email)) {
            return Result.error("邮箱格式不正确");
        }
        
        return Result.success("验证通过");
    }
    
    /**
     * 检查用户是否可以登录
     * @param userDTO 用户数据传输对象
     * @return 检查结果
     */
    public Result<String> checkUserCanLogin(UserDTO userDTO) {
        if (!isActive(userDTO.getStatus())) {
            return Result.error("账户已被禁用，请联系管理员");
        }
        
        return Result.success("可以登录");
    }
    
    /**
     * 设置新用户的默认值
     * @param userDTO 用户数据传输对象
     */
    public void setDefaultsForNewUser(UserDTO userDTO) {
        userDTO.setCreateTime(LocalDateTime.now());
        userDTO.setUpdateTime(LocalDateTime.now());
        userDTO.setStatus(1); // 默认启用
        userDTO.setRole("USER"); // 默认普通用户
    }
    
    /**
     * 清除用户敏感信息
     * @param userDTO 用户数据传输对象
     */
    public void clearSensitiveInfo(UserDTO userDTO) {
        if (userDTO != null) {
            userDTO.setPassword(null);
        }
    }
    
    /**
     * 检查用户是否有管理员权限
     * @param userDTO 用户数据传输对象
     * @return 是否有管理员权限
     */
    public boolean hasAdminPermission(UserDTO userDTO) {
        return userDTO != null && isAdmin(userDTO.getRole());
    }
    
    /**
     * 检查用户是否可以修改自己的账户
     * @param currentUser 当前用户
     * @param targetUserId 目标用户ID
     * @return 是否可以修改
     */
    public boolean canModifyOwnAccount(UserDTO currentUser, Integer targetUserId) {
        return currentUser != null && currentUser.getId().equals(targetUserId);
    }
    
    // 业务规则：用户名长度验证
    private boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }
    
    // 业务规则：密码长度验证
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    // 业务规则：邮箱格式验证
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // 邮箱可选
        }
        return email.contains("@");
    }
    
    // 业务规则：用户状态验证
    private boolean isActive(Integer status) {
        return status != null && status == 1;
    }
    
    // 业务规则：是否为管理员
    private boolean isAdmin(String role) {
        return "ADMIN".equals(role);
    }
}