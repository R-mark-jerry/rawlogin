package com.rawlogin.application.impl;

import com.rawlogin.application.UserApplicationService;
import com.rawlogin.domain.model.User;
import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.domain.repository.UserRepository;
import com.rawlogin.domain.service.UserDomainService;
import com.rawlogin.application.converter.UserConverter;
import com.rawlogin.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户应用服务实现
 * 编排应用层的用例和业务流程
 */
@Service
@Transactional
public class UserApplicationServiceImpl implements UserApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserApplicationServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDomainService userDomainService;
    
    @Override
    public Result<UserDTO> login(String username, String password) {
        try {
            logger.info("用户登录尝试: {}", username);
            
            // 创建用户对象
            User user = new User(username, password);
            
            // 验证登录信息
            Result<String> validationResult = userDomainService.validateLogin(user);
            if (!validationResult.isSuccess()) {
                return Result.error(validationResult.getMessage());
            }
            
            // 查找用户
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                logger.warn("用户不存在: {}", username);
                return Result.error("用户名或密码错误");
            }
            
            User foundUser = userOpt.get();
            
            // 检查用户是否可以登录
            Result<String> checkResult = userDomainService.checkUserCanLogin(foundUser);
            if (!checkResult.isSuccess()) {
                return Result.error(checkResult.getMessage());
            }
            
            // 更新最后登录时间
            userRepository.updateLastLoginTime(foundUser.getId());
            
            // 清除敏感信息
            userDomainService.clearSensitiveInfo(foundUser);
            
            // 转换为DTO
            UserDTO userDTO = UserConverter.toDTO(foundUser);
            
            logger.info("用户登录成功: {}", username);
            return Result.success("登录成功", userDTO);
            
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserDTO> register(User user) {
        try {
            logger.info("用户注册尝试: {}", user.getUsername());
            
            // 验证注册信息
            Result<String> validationResult = userDomainService.validateRegistration(user);
            if (!validationResult.isSuccess()) {
                return Result.error(validationResult.getMessage());
            }
            
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return Result.error("用户名已存在");
            }
            
            // 设置默认值
            userDomainService.setDefaultsForNewUser(user);
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            // 清除敏感信息
            userDomainService.clearSensitiveInfo(savedUser);
            
            // 转换为DTO
            UserDTO userDTO = UserConverter.toDTO(savedUser);
            
            logger.info("用户注册成功: {}", user.getUsername());
            return Result.success("注册成功", userDTO);
            
        } catch (Exception e) {
            logger.error("注册过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserDTO> getCurrentUser(Integer userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            User user = userOpt.get();
            userDomainService.clearSensitiveInfo(user);
            
            // 转换为DTO
            UserDTO userDTO = UserConverter.toDTO(user);
            
            return Result.success("获取用户信息成功", userDTO);
        } catch (Exception e) {
            logger.error("获取当前用户信息时发生异常: {}", userId, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            // 清除所有用户的敏感信息
            users.forEach(userDomainService::clearSensitiveInfo);
            
            // 转换为DTO列表
            List<UserDTO> userDTOs = users.stream()
                    .map(UserConverter::toDTO)
                    .collect(Collectors.toList());
            
            return Result.success("获取用户列表成功", userDTOs);
        } catch (Exception e) {
            logger.error("获取用户列表时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserDTO> getUserById(Integer id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            User user = userOpt.get();
            userDomainService.clearSensitiveInfo(user);
            
            // 转换为DTO
            UserDTO userDTO = UserConverter.toDTO(user);
            
            return Result.success("找到用户", userDTO);
        } catch (Exception e) {
            logger.error("根据ID查找用户时发生异常: {}", id, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserDTO> updateUser(User user) {
        try {
            // 参数验证
            if (user.getId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            // 检查用户名是否已存在（排除当前用户）
            if (userRepository.existsByUsernameAndExcludeId(user.getUsername(), user.getId())) {
                return Result.error("用户名已存在");
            }
            
            // 更新用户
            User updatedUser = userRepository.update(user);
            userDomainService.clearSensitiveInfo(updatedUser);
            
            // 转换为DTO
            UserDTO userDTO = UserConverter.toDTO(updatedUser);
            
            logger.info("用户更新成功: {}", user.getUsername());
            return Result.success("更新成功", userDTO);
        } catch (Exception e) {
            logger.error("更新用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<Void> deleteUser(Integer id) {
        try {
            // 检查用户是否存在
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            // 删除用户
            boolean success = userRepository.deleteById(id);
            if (!success) {
                return Result.error("删除失败，请稍后再试");
            }
            
            logger.info("用户删除成功: {}", userOpt.get().getUsername());
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
            int deletedCount = userRepository.deleteByIds(ids);
            if (deletedCount == 0) {
                return Result.error("删除失败，请稍后再试");
            }
            
            logger.info("批量删除用户成功，删除数量: {}", deletedCount);
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("批量删除用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<List<UserDTO>> searchUsers(String username, String email, Integer status, String role) {
        try {
            List<User> users = userRepository.findByCondition(username, email, status, role);
            
            // 清除所有用户的敏感信息
            users.forEach(userDomainService::clearSensitiveInfo);
            
            // 转换为DTO列表
            List<UserDTO> userDTOs = users.stream()
                    .map(UserConverter::toDTO)
                    .collect(Collectors.toList());
            
            return Result.success("查询成功", userDTOs);
        } catch (Exception e) {
            logger.error("根据条件查询用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
}