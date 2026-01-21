package com.rawlogin.service;

import com.rawlogin.common.Result;
import com.rawlogin.service.entity.User;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含用户信息
     */
    Result<User> login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果，包含用户信息
     */
    Result<User> register(User user);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 查找结果，包含用户信息
     */
    Result<User> findByUsername(String username);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 查找结果，包含用户信息
     */
    Result<User> findById(Integer id);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 更新结果
     */
    Result<Void> updateLastLoginTime(Integer userId);
}