package org.example.service;

import org.example.service.entity.User;
import org.example.common.Result;

/**
 * 用户服务接口
 * 简化版的服务接口，适合常见的分层架构
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Result<User> login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册结果
     */
    Result<User> register(User user);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 查找结果
     */
    Result<User> findByUsername(String username);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 查找结果
     */
    Result<User> findById(Integer id);
}
