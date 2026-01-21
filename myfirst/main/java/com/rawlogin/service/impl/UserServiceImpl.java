package com.rawlogin.service.impl;

import com.rawlogin.common.Result;
import com.rawlogin.repository.UserRepository;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Result<User> login(String username, String password) {
        try {
            logger.info("用户登录尝试: {}", username);
            
            // 参数验证
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            
            // 查找用户
            Result<User> userResult = userRepository.findByUsername(username);
            if (!userResult.isSuccess()) {
                logger.warn("用户不存在: {}", username);
                return Result.error("用户名或密码错误");
            }
            
            User user = userResult.getData();
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("密码错误: {}", username);
                return Result.error("用户名或密码错误");
            }
            
            // 检查用户状态
            if (user.getStatus() != null && user.getStatus() != 1) {
                logger.warn("用户状态异常: {}, status: {}", username, user.getStatus());
                return Result.error("账户已被禁用，请联系管理员");
            }
            
            // 更新最后登录时间
            userRepository.updateLastLoginTime(user.getId());
            
            // 重新查询用户信息以获取更新后的最后登录时间
            Result<User> updatedUserResult = userRepository.findByUsername(username);
            if (updatedUserResult.isSuccess()) {
                user = updatedUserResult.getData();
            }
            
            // 清除密码信息，不返回给前端
            user.setPassword(null);
            
            logger.info("用户登录成功: {}", username);
            return Result.success("登录成功", user);
            
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> register(User user) {
        try {
            logger.info("用户注册尝试: {}", user.getUsername());
            
            // 参数验证
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            
            if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
                return Result.error("用户名长度必须在3-50个字符之间");
            }
            
            if (user.getPassword().length() < 6) {
                return Result.error("密码长度不能少于6位");
            }
            
            // 检查用户名是否已存在
            Result<User> existUserResult = userRepository.findByUsername(user.getUsername());
            if (existUserResult.isSuccess()) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return Result.error("用户名已存在");
            }
            
            // 密码加密
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            
            // 设置默认值
            user.setCreateTime(LocalDateTime.now());
            user.setStatus(1); // 默认状态为启用
            
            // 保存用户
            Result<User> saveResult = userRepository.save(user);
            if (!saveResult.isSuccess()) {
                return Result.error("注册失败，请稍后再试");
            }
            
            // 清除密码信息，不返回给前端
            User savedUser = saveResult.getData();
            savedUser.setPassword(null);
            
            logger.info("用户注册成功: {}", user.getUsername());
            return Result.success("注册成功", savedUser);
            
        } catch (Exception e) {
            logger.error("注册过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> findByUsername(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("根据用户名查找用户时发生异常: {}", username, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> findById(Integer id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            logger.error("根据ID查找用户时发生异常: {}", id, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<Void> updateLastLoginTime(Integer userId) {
        try {
            return userRepository.updateLastLoginTime(userId);
        } catch (Exception e) {
            logger.error("更新用户最后登录时间时发生异常: {}", userId, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
}