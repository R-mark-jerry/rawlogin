package com.rawlogin.domain.service;

import com.rawlogin.domain.model.User;
import com.rawlogin.common.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户领域服务
 * 包含核心业务逻辑和业务规则
 */
@Service
public class UserDomainService {
    
    /**
     * 验证用户登录信息
     * @param user 用户对象
     * @return 验证结果
     */
    public Result<String> validateLogin(User user) {
        if (!user.isValidUsername()) {
            return Result.error("用户名格式不正确");
        }
        
        if (!user.isValidPassword()) {
            return Result.error("密码格式不正确");
        }
        
        return Result.success("验证通过");
    }
    
    /**
     * 验证用户注册信息
     * @param user 用户对象
     * @return 验证结果
     */
    public Result<String> validateRegistration(User user) {
        if (!user.isValidUsername()) {
            return Result.error("用户名长度必须在3-50个字符之间");
        }
        
        if (!user.isValidPassword()) {
            return Result.error("密码长度不能少于6位");
        }
        
        if (!user.isValidEmail()) {
            return Result.error("邮箱格式不正确");
        }
        
        return Result.success("验证通过");
    }
    
    /**
     * 检查用户是否可以登录
     * @param user 用户对象
     * @return 检查结果
     */
    public Result<String> checkUserCanLogin(User user) {
        if (!user.isActive()) {
            return Result.error("账户已被禁用，请联系管理员");
        }
        
        return Result.success("可以登录");
    }
    
    /**
     * 设置新用户的默认值
     * @param user 用户对象
     */
    public void setDefaultsForNewUser(User user) {
        user.setDefaultsForNewUser();
    }
    
    /**
     * 清除用户敏感信息
     * @param user 用户对象
     */
    public void clearSensitiveInfo(User user) {
        user.clearSensitiveInfo();
    }
    
    /**
     * 检查用户是否有管理员权限
     * @param user 用户对象
     * @return 是否有管理员权限
     */
    public boolean hasAdminPermission(User user) {
        return user != null && user.isAdmin();
    }
    
    /**
     * 检查用户是否可以修改自己的账户
     * @param currentUser 当前用户
     * @param targetUserId 目标用户ID
     * @return 是否可以修改
     */
    public boolean canModifyOwnAccount(User currentUser, Integer targetUserId) {
        return currentUser != null && currentUser.getId().equals(targetUserId);
    }
}