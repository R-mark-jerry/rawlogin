package com.rawlogin.application.impl;

import com.rawlogin.application.UserApplicationService;
import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.interfaces.vo.UserVO;
import com.rawlogin.domain.repository.UserRepository;
import com.rawlogin.domain.service.UserDomainService;
import com.rawlogin.domain.model.User;
import com.rawlogin.application.converter.UserConverter;
import com.rawlogin.common.Result;
import com.rawlogin.util.PasswordUtil;
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
    public Result<UserVO> login(String username, String password) {
        try {
            logger.info("用户登录尝试: {}", username);
            
            // 验证登录信息
            Result<String> validationResult = userDomainService.validateLogin(username, password);
            if (!validationResult.isSuccess()) {
                return Result.error(validationResult.getMessage());
            }
            
            // 查找用户
            Optional<UserDTO> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                logger.warn("用户不存在: {}", username);
                return Result.error("用户名或密码错误");
            }
            
            UserDTO foundUser = userOpt.get();
            
            // 检查密码是否匹配（使用加密比较）
            if (!PasswordUtil.matches(password, foundUser.getPassword())) {
                return Result.error("用户名或密码错误");
            }
            
            // 检查用户是否可以登录
            Result<String> checkResult = userDomainService.checkUserCanLogin(foundUser);
            if (!checkResult.isSuccess()) {
                return Result.error(checkResult.getMessage());
            }
            
            // 更新最后登录时间
            userRepository.updateLastLoginTime(foundUser.getId());
            
            // 清除敏感信息
            userDomainService.clearSensitiveInfo(foundUser);
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(foundUser);
            
            logger.info("用户登录成功: {}", username);
            return Result.success("登录成功", userVO);
            
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserVO> register(UserDTO userDTO) {
        try {
            logger.info("用户注册尝试: {}", userDTO.getUsername());
            
            // 验证注册信息
            Result<String> validationResult = userDomainService.validateRegistration(
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getEmail()
            );
            if (!validationResult.isSuccess()) {
                return Result.error(validationResult.getMessage());
            }
            
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(userDTO.getUsername())) {
                logger.warn("用户名已存在: {}", userDTO.getUsername());
                return Result.error("用户名已存在");
            }
            
            // 设置默认值
            userDomainService.setDefaultsForNewUser(userDTO);
            
            // 加密密码
            String encryptedPassword = PasswordUtil.encode(userDTO.getPassword());
            userDTO.setPassword(encryptedPassword);
            
            // 保存用户
            UserDTO savedUser = userRepository.save(userDTO);
            
            // 清除敏感信息
            userDomainService.clearSensitiveInfo(savedUser);
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(savedUser);
            
            logger.info("用户注册成功: {}", userDTO.getUsername());
            return Result.success("注册成功", userVO);
            
        } catch (Exception e) {
            logger.error("注册过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    /**
     * 用户注册方法（重载，接受User对象）
     * @param user 用户领域模型
     * @return 注册结果
     */
    public Result<UserVO> register(User user) {
        try {
            logger.info("用户注册尝试: {}", user.getUsername());
            
            // 验证注册信息
            Result<String> validationResult = userDomainService.validateRegistration(
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
            );
            
            if (!validationResult.isSuccess()) {
                return Result.error(validationResult.getMessage());
            }
            
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                logger.warn("用户名已存在: {}", user.getUsername());
                return Result.error("用户名已存在");
            }
            
            // 转换为DTO
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setEmail(user.getEmail());
            
            // 设置默认值
            userDomainService.setDefaultsForNewUser(userDTO);
            
            // 加密密码
            String encryptedPassword = PasswordUtil.encode(userDTO.getPassword());
            userDTO.setPassword(encryptedPassword);
            
            // 保存用户
            UserDTO savedUser = userRepository.save(userDTO);
            
            // 清除敏感信息
            userDomainService.clearSensitiveInfo(savedUser);
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(savedUser);
            
            logger.info("用户注册成功: {}", user.getUsername());
            return Result.success("注册成功", userVO);
            
        } catch (Exception e) {
            logger.error("注册过程中发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserVO> getCurrentUser(Integer userId) {
        try {
            Optional<UserDTO> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            UserDTO userDTO = userOpt.get();
            userDomainService.clearSensitiveInfo(userDTO);
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(userDTO);
            
            return Result.success("获取用户信息成功", userVO);
        } catch (Exception e) {
            logger.error("获取当前用户信息时发生异常: {}", userId, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<List<UserVO>> getAllUsers() {
        try {
            List<UserDTO> userDTOs = userRepository.findAll();
            
            // 清除所有用户的敏感信息
            userDTOs.forEach(userDomainService::clearSensitiveInfo);
            
            // 转换为VO列表
            List<UserVO> userVOs = userDTOs.stream()
                    .map(UserConverter::toVO)
                    .collect(Collectors.toList());
            
            return Result.success("获取用户列表成功", userVOs);
        } catch (Exception e) {
            logger.error("获取用户列表时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserVO> getUserById(Integer id) {
        try {
            Optional<UserDTO> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            UserDTO userDTO = userOpt.get();
            userDomainService.clearSensitiveInfo(userDTO);
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(userDTO);
            
            return Result.success("找到用户", userVO);
        } catch (Exception e) {
            logger.error("根据ID查找用户时发生异常: {}", id, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<UserVO> updateUser(UserDTO userDTO) {
        try {
            // 参数验证
            if (userDTO.getId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            // 检查用户名是否已存在（排除当前用户）
            if (userRepository.existsByUsernameAndExcludeId(userDTO.getUsername(), userDTO.getId())) {
                return Result.error("用户名已存在");
            }
            
            // 如果提供了新密码，则加密
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                String encryptedPassword = PasswordUtil.encode(userDTO.getPassword());
                userDTO.setPassword(encryptedPassword);
            }
            
            // 更新用户
            UserDTO updatedUser = userRepository.update(userDTO);
            userDomainService.clearSensitiveInfo(updatedUser);
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(updatedUser);
            
            logger.info("用户更新成功: {}", userDTO.getUsername());
            return Result.success("更新成功", userVO);
        } catch (Exception e) {
            logger.error("更新用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<Void> deleteUser(Integer id) {
        try {
            // 检查用户是否存在
            Optional<UserDTO> userOpt = userRepository.findById(id);
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
    public Result<List<UserVO>> searchUsers(String username, String email, Integer status, String role) {
        try {
            List<UserDTO> userDTOs = userRepository.findByCondition(username, email, status, role);
            
            // 清除所有用户的敏感信息
            userDTOs.forEach(userDomainService::clearSensitiveInfo);
            
            // 转换为VO列表
            List<UserVO> userVOs = userDTOs.stream()
                    .map(UserConverter::toVO)
                    .collect(Collectors.toList());
            
            return Result.success("查询成功", userVOs);
        } catch (Exception e) {
            logger.error("根据条件查询用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
}