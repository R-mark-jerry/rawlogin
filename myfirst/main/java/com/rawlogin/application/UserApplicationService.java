package com.rawlogin.application;

import com.rawlogin.domain.model.User;
import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.common.Result;

import java.util.List;

/**
 * 用户应用服务接口
 * 定义应用层的用例和业务流程
 */
public interface UserApplicationService {
    
    /**
     * 用户登录用例
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Result<UserDTO> login(String username, String password);
    
    /**
     * 用户注册用例
     * @param user 用户对象
     * @return 注册结果
     */
    Result<UserDTO> register(User user);
    
    /**
     * 获取当前用户信息用例
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<UserDTO> getCurrentUser(Integer userId);
    
    /**
     * 获取所有用户用例
     * @return 用户列表
     */
    Result<List<UserDTO>> getAllUsers();
    
    /**
     * 根据ID获取用户用例
     * @param id 用户ID
     * @return 用户信息
     */
    Result<UserDTO> getUserById(Integer id);
    
    /**
     * 更新用户信息用例
     * @param user 用户对象
     * @return 更新结果
     */
    Result<UserDTO> updateUser(User user);
    
    /**
     * 删除用户用例
     * @param id 用户ID
     * @return 删除结果
     */
    Result<Void> deleteUser(Integer id);
    
    /**
     * 批量删除用户用例
     * @param ids 用户ID列表
     * @return 删除结果
     */
    Result<Void> batchDeleteUsers(List<Integer> ids);
    
    /**
     * 根据条件查询用户用例
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param status 状态（可选）
     * @param role 角色（可选）
     * @return 查询结果
     */
    Result<List<UserDTO>> searchUsers(String username, String email, Integer status, String role);
}