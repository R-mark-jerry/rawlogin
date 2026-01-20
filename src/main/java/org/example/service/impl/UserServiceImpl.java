package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 使用Spring Data JPA进行数据访问
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public User login(String username, String password) {
        logger.info("用户登录尝试: {}", username);
        
        try {
            // 根据用户名和状态查找用户
            User user = userRepository.findByUsernameAndStatus(username, 1).orElse(null);
            
            if (user == null) {
                logger.warn("用户不存在: {}", username);
                return null;
            }
            
            // 验证密码
            boolean passwordMatch = PasswordUtil.verify(password, user.getPassword());
            
            if (passwordMatch) {
                // 更新最后登录时间
                updateLastLoginTime(username);
                logger.info("用户登录成功: {}", username);
                return user;
            } else {
                logger.warn("密码不匹配: 输入密码={}, 数据库密码={}", password, user.getPassword());
                return null;
            }
        } catch (Exception e) {
            logger.error("用户登录过程中发生异常: {}", username, e);
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        logger.debug("根据用户名查找用户: {}", username);
        
        try {
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            logger.error("根据用户名查找用户失败: {}", username, e);
            return null;
        }
    }

    @Override
    public boolean register(User user) {
        logger.info("注册新用户: {}", user.getUsername());
        
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return false;
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
            } else {
                logger.error("用户注册失败: {}", user.getUsername());
            }
            
            return success;
        } catch (Exception e) {
            logger.error("用户注册失败: {}", user.getUsername(), e);
            return false;
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
