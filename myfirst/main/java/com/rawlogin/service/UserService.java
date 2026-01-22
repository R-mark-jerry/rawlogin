package com.rawlogin.service;

import com.rawlogin.common.Result;
import com.rawlogin.service.entity.User;

import java.util.List;

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
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    Result<List<User>> findAllUsers();
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    Result<User> updateUser(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    Result<Void> deleteUser(Integer id);
    
    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 删除结果
     */
    Result<Void> batchDeleteUsers(List<Integer> ids);
    
    /**
     * 根据条件查询用户
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param status 用户状态（可选）
     * @return 查询结果
     */
    Result<List<User>> findUsersByCondition(String username, String email, Integer status);
}