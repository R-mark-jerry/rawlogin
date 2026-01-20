package org.example.service;

import org.example.model.User;

public interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回User对象，失败返回null
     */
    User login(String username, String password);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    User findByUsername(String username);
    
    /**
     * 注册新用户
     * @param user 用户对象
     * @return 注册成功返回true，失败返回false
     */
    boolean register(User user);
}
