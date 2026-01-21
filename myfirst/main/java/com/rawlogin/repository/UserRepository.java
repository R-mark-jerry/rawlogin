package com.rawlogin.repository;

import com.rawlogin.common.Result;
import com.rawlogin.service.entity.User;

/**
 * 用户数据访问接口
 * 定义用户相关的数据库操作
 */
public interface UserRepository {
    
    /**
     * 保存用户
     * @param user 用户信息
     * @return 保存结果，包含用户信息
     */
    Result<User> save(User user);
    
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
     * 检查用户名是否存在
     * @param username 用户名
     * @return 检查结果，true表示存在，false表示不存在
     */
    boolean existsByUsername(String username);
}