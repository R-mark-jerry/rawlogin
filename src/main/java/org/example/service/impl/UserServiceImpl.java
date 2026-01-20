package org.example.service.impl;

import org.example.service.UserService;
import org.example.service.entity.User;
import org.example.common.Result;
import org.example.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 简化版的服务实现，适合常见的分层架构
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private org.example.repository.UserRepository userRepository;
    
    @Override
    public Result<User> login(String username, String password) {
        logger.info("用户登录尝试: {}", username);
        
        try {
            // 验证输入参数
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                logger.warn("登录失败: 用户名或密码为空");
                return Result.error("用户名和密码不能为空");
            }
            
            // 根据用户名和状态查找用户
            User user = userRepository.findByUsernameAndStatus(username, 1).orElse(null);
            
            if (user == null) {
                logger.warn("登录失败: 用户不存在 - {}", username);
                return Result.error("用户名或密码错误");
            }
            
            // 验证密码
            boolean passwordMatch = false;
            try {
                logger.debug("尝试验证密码 - 输入密码: {}, 数据库密码: {}", password, user.getPassword());
                passwordMatch = PasswordUtil.verify(password, user.getPassword());
                logger.info("密码验证结果: {}", passwordMatch);
            } catch (Exception e) {
                logger.error("密码验证异常: {}", e.getMessage(), e);
                // 如果BCrypt验证失败，尝试明文比较
                passwordMatch = password.equals(user.getPassword());
                logger.info("明文密码比较结果: {}", passwordMatch);
            }
            
            if (passwordMatch) {
                // 登录成功
                logger.info("用户登录成功: {}", username);
                
                // 更新最后登录时间
                updateLastLoginTime(username);
                
                return Result.success(user);
            } else {
                // 登录失败
                logger.warn("登录失败: 用户名或密码错误 - {}", username);
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> register(User user) {
        logger.info("用户注册尝试: {}", user.getUsername());
        
        try {
            // 验证输入参数
            if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                logger.warn("注册失败: 用户名或密码为空");
                return Result.error("用户名和密码不能为空");
            }
            
            if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
                logger.warn("注册失败: 用户名长度不符合要求 - {}", user.getUsername());
                return Result.error("用户名长度必须在3-50个字符之间");
            }
            
            if (user.getPassword().length() < 6) {
                logger.warn("注册失败: 密码长度不足 - {}", user.getUsername());
                return Result.error("密码长度不能少于6位");
            }
            
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                logger.warn("注册失败: 用户名已存在 - {}", user.getUsername());
                return Result.error("用户名已存在，请选择其他用户名");
            }
            
            // 加密密码
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
            
            // 设置创建时间和状态
            user.setCreateTime(LocalDateTime.now());
            user.setStatus(1); // 启用状态
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            boolean success = savedUser != null && savedUser.getId() != null;
            if (success) {
                logger.info("用户注册成功: {}", user.getUsername());
                return Result.success(savedUser);
            } else {
                logger.error("用户注册失败: {}", user.getUsername());
                return Result.error("注册失败，请稍后再试");
            }
        } catch (Exception e) {
            logger.error("用户注册过程中发生异常: {}", user.getUsername(), e);
            return Result.error("注册失败，系统错误，请稍后再试");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Result<User> findByUsername(String username) {
        logger.debug("根据用户名查找用户: {}", username);
        
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            logger.error("根据用户名查找用户失败: {}", username, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Result<User> findById(Integer id) {
        logger.debug("根据ID查找用户: {}", id);
        
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            logger.error("根据ID查找用户失败: {}", id, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    /**
     * 更新用户最后登录时间
     * @param username 用户名
     */
    private void updateLastLoginTime(String username) {
        try {
            int updatedRows = userRepository.updateLastLoginTime(username);
            if (updatedRows > 0) {
                logger.debug("更新用户最后登录时间成功: {}", username);
            } else {
                logger.warn("更新用户最后登录时间失败，用户不存在: {}", username);
            }
        } catch (Exception e) {
            logger.error("更新用户最后登录时间失败: {}", username, e);
        }
    }
}
