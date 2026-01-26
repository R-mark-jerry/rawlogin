package com.rawlogin.domain.repository;

import com.rawlogin.application.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * 用户领域仓储接口
 * 定义用户数据访问的抽象接口
 */
public interface UserRepository {
    
    /**
     * 保存用户
     * @param userDTO 用户数据传输对象
     * @return 保存后的用户数据传输对象
     */
    UserDTO save(UserDTO userDTO);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户数据传输对象
     */
    Optional<UserDTO> findById(Integer id);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户数据传输对象
     */
    Optional<UserDTO> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     * @param username 用户名
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    boolean existsByUsernameAndExcludeId(String username, Integer excludeId);
    
    /**
     * 更新用户
     * @param userDTO 用户数据传输对象
     * @return 更新后的用户数据传输对象
     */
    UserDTO update(UserDTO userDTO);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteById(Integer id);
    
    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 删除的数量
     */
    int deleteByIds(List<Integer> ids);
    
    /**
     * 查找所有用户
     * @return 用户数据传输对象列表
     */
    List<UserDTO> findAll();
    
    /**
     * 根据条件查询用户
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param status 状态（可选）
     * @param role 角色（可选）
     * @return 用户数据传输对象列表
     */
    List<UserDTO> findByCondition(String username, String email, Integer status, String role);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 是否更新成功
     */
    boolean updateLastLoginTime(Integer userId);
}