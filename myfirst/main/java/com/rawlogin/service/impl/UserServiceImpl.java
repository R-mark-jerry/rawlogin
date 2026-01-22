package com.rawlogin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rawlogin.common.Result;
import com.rawlogin.mapper.UserMapper;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserMapper userMapper;
    
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
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User user = userMapper.selectOne(queryWrapper);
            
            if (user == null) {
                logger.warn("用户不存在: {}", username);
                return Result.error("用户名或密码错误");
            }
            
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
            userMapper.updateLastLoginTime(user.getId());
            
            // 重新查询用户信息以获取更新后的最后登录时间
            user = userMapper.selectOne(queryWrapper);
            
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
            QueryWrapper<User> existQueryWrapper = new QueryWrapper<>();
            existQueryWrapper.eq("username", user.getUsername());
            User existUser = userMapper.selectOne(existQueryWrapper);
            if (existUser != null) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return Result.error("用户名已存在");
            }
            
            // 密码加密
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            
            // 设置默认值
            user.setCreateTime(LocalDateTime.now());
            user.setStatus(1); // 默认状态为启用
            
            // 处理email和realName为空字符串的情况，转换为null
            if (user.getEmail() != null && user.getEmail().trim().isEmpty()) {
                user.setEmail(null);
            }
            if (user.getRealName() != null && user.getRealName().trim().isEmpty()) {
                user.setRealName(null);
            }
            
            // 保存用户
            int result = userMapper.insert(user);
            if (result <= 0) {
                return Result.error("注册失败，请稍后再试");
            }
            
            // 清除密码信息，不返回给前端
            user.setPassword(null);
            
            logger.info("用户注册成功: {}", user.getUsername());
            return Result.success("注册成功", user);
            
        } catch (Exception e) {
            logger.error("注册过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> findByUsername(String username) {
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User user = userMapper.selectOne(queryWrapper);
            
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            return Result.success("找到用户", user);
        } catch (Exception e) {
            logger.error("根据用户名查找用户时发生异常: {}", username, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> findById(Integer id) {
        try {
            User user = userMapper.selectById(id);
            
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            return Result.success("找到用户", user);
        } catch (Exception e) {
            logger.error("根据ID查找用户时发生异常: {}", id, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<Void> updateLastLoginTime(Integer userId) {
        try {
            int result = userMapper.updateLastLoginTime(userId);
            
            if (result > 0) {
                return Result.success("更新成功");
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            logger.error("更新用户最后登录时间时发生异常: {}", userId, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    @Override
    public Result<List<User>> findAllUsers() {
        try {
            List<User> users = userMapper.selectList(null);
            return Result.success("获取用户列表成功", users);
        } catch (Exception e) {
            logger.error("获取用户列表时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }

    @Override
    public Result<User> updateUser(User user) {
        try {
            // 参数验证
            if (user.getId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            
            // 检查用户名是否已存在（排除当前用户）
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername())
                      .ne("id", user.getId());
            User existUser = userMapper.selectOne(queryWrapper);
            if (existUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 如果密码不为空，则加密密码
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
            } else {
                // 如果密码为空，保持原密码不变
                User existingUser = userMapper.selectById(user.getId());
                if (existingUser != null) {
                    user.setPassword(existingUser.getPassword());
                }
            }
            
            // 更新用户
            int result = userMapper.updateById(user);
            if (result <= 0) {
                return Result.error("更新失败，请稍后再试");
            }
            
            // 清除密码信息，不返回给前端
            user.setPassword(null);
            
            logger.info("用户更新成功: {}", user.getUsername());
            return Result.success("更新成功", user);
        } catch (Exception e) {
            logger.error("更新用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }

    @Override
    public Result<Void> deleteUser(Integer id) {
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 删除用户
            int result = userMapper.deleteById(id);
            if (result <= 0) {
                return Result.error("删除失败，请稍后再试");
            }
            
            logger.info("用户删除成功: {}", user.getUsername());
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("删除用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }

    @Override
    public Result<Void> batchDeleteUsers(List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("用户ID列表不能为空");
            }
            
            // 批量删除用户
            int result = userMapper.deleteBatchIds(ids);
            if (result == 0) {
                return Result.error("删除失败，请稍后再试");
            }
            
            logger.info("批量删除用户成功，删除数量: {}", result);
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("批量删除用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }

    @Override
    public Result<List<User>> findUsersByCondition(String username, String email, Integer status) {
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            
            // 根据用户名查询
            if (username != null && !username.trim().isEmpty()) {
                queryWrapper.like("username", username);
            }
            
            // 根据邮箱查询
            if (email != null && !email.trim().isEmpty()) {
                queryWrapper.like("email", email);
            }
            
            // 根据状态查询
            if (status != null) {
                queryWrapper.eq("status", status);
            }
            
            // 按ID降序排列
            queryWrapper.orderByDesc("id");
            
            List<User> users = userMapper.selectList(queryWrapper);
            return Result.success("查询成功", users);
        } catch (Exception e) {
            logger.error("根据条件查询用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
}