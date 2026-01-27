package com.rawlogin.domain.service;

import com.rawlogin.domain.repository.UserRepository;
import com.rawlogin.domain.repository.RoleRepository;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.infrastructure.po.UserPO;
import com.rawlogin.infrastructure.po.UserRolePO;
import com.rawlogin.infrastructure.persistence.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色领域服务
 * 处理用户与角色的关联关系
 */
@Service
public class UserRoleDomainService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<RoleDTO> getUserRoles(Integer userId) {
        return roleRepository.findRolesByUserId(userId);
    }
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Integer userId, List<Integer> roleIds) {
        // 检查用户是否存在
        if (userRepository.findById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查所有角色是否存在
        for (Integer roleId : roleIds) {
            if (roleRepository.findById(roleId) == null) {
                throw new RuntimeException("角色ID " + roleId + " 不存在");
            }
        }
        
        // 先删除用户原有的所有角色
        removeAllRolesFromUser(userId);
        
        // 分配新角色
        for (Integer roleId : roleIds) {
            UserRolePO userRole = new UserRolePO();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
        
        // 同步更新用户表中的角色字段（选择优先级最高的角色）
        if (!roleIds.isEmpty()) {
            // 获取角色信息，按优先级排序（ADMIN优先）
            List<RoleDTO> assignedRoles = roleIds.stream()
                    .map(roleId -> roleRepository.findById(roleId))
                    .filter(role -> role != null)
                    .collect(java.util.stream.Collectors.toList());
            
            String primaryRole = determinePrimaryRole(assignedRoles);
            
            // 更新用户表的角色字段，使用PO对象
            UserPO userPO = userRoleMapper.selectUserPOById(userId);
            if (userPO == null) {
                throw new RuntimeException("用户不存在");
            }
            userPO.setRole(primaryRole);
            userRoleMapper.updateUserRoleById(userPO);
        }
        
        return true;
    }
    
    /**
     * 确定主要角色（按优先级）
     * @param roles 角色列表
     * @return 主要角色代码
     */
    private String determinePrimaryRole(List<RoleDTO> roles) {
        // 如果有ADMIN角色，优先使用ADMIN
        for (RoleDTO role : roles) {
            if (role != null && "ADMIN".equals(role.getCode())) {
                return "ADMIN";
            }
        }
        
        // 否则返回第一个角色
        if (!roles.isEmpty() && roles.get(0) != null) {
            return roles.get(0).getCode();
        }
        
        // 默认返回USER
        return "USER";
    }
    
    /**
     * 移除用户的所有角色
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllRolesFromUser(Integer userId) {
        return userRoleMapper.deleteByUserId(userId) > 0;
    }
    
    /**
     * 移除用户的指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRoleFromUser(Integer userId, Integer roleId) {
        return userRoleMapper.deleteByUserIdAndRoleId(userId, roleId) > 0;
    }
    
    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleCode 角色代码
     * @return 是否拥有角色
     */
    public boolean userHasRole(Integer userId, String roleCode) {
        List<RoleDTO> userRoles = getUserRoles(userId);
        return userRoles.stream()
                .anyMatch(role -> role.getCode().equals(roleCode));
    }
    
    /**
     * 获取角色的所有用户ID
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    public List<Integer> getUserIdsByRoleId(Integer roleId) {
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }
    
    /**
     * 获取用户的所有角色代码
     * @param userId 用户ID
     * @return 角色代码列表
     */
    public List<String> getUserRoleCodes(Integer userId) {
        List<RoleDTO> userRoles = getUserRoles(userId);
        return userRoles.stream()
                .map(RoleDTO::getCode)
                .collect(Collectors.toList());
    }
}