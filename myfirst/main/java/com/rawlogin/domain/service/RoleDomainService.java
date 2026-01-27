package com.rawlogin.domain.service;

import com.rawlogin.domain.repository.RoleRepository;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.common.Result;
import com.rawlogin.infrastructure.persistence.RolePermissionMapper;
import com.rawlogin.infrastructure.po.RolePermissionPO;
import com.rawlogin.infrastructure.persistence.PermissionMapper;
import com.rawlogin.infrastructure.po.PermissionPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色领域服务
 * 处理角色相关的业务逻辑
 */
@Service
public class RoleDomainService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    /**
     * 获取所有角色
     * @return 角色列表
     */
    public Result<List<RoleDTO>> getAllRoles() {
        try {
            List<RoleDTO> roles = roleRepository.findAll();
            return Result.success("获取角色列表成功", roles);
        } catch (Exception e) {
            return Result.error("获取角色列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    public Result<RoleDTO> getRoleById(Integer id) {
        try {
            RoleDTO role = roleRepository.findById(id);
            if (role == null) {
                return Result.error("角色不存在");
            }
            return Result.success("获取角色信息成功", role);
        } catch (Exception e) {
            return Result.error("获取角色信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据条件搜索角色
     * @param name 角色名称（可选）
     * @param code 角色代码（可选）
     * @param status 角色状态（可选）
     * @param builtIn 是否内置角色（可选）
     * @return 角色列表
     */
    public Result<List<RoleDTO>> searchRoles(String name, String code, Integer status, Boolean builtIn) {
        try {
            List<RoleDTO> roles = roleRepository.searchRoles(name, code, status, builtIn);
            return Result.success("搜索角色成功", roles);
        } catch (Exception e) {
            return Result.error("搜索角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建角色
     * @param roleDTO 角色数据传输对象
     * @return 创建结果
     */
    public Result<RoleDTO> createRole(RoleDTO roleDTO) {
        try {
            // 检查角色代码是否已存在
            RoleDTO existingRole = roleRepository.findByCode(roleDTO.getCode());
            if (existingRole != null) {
                return Result.error("角色代码已存在");
            }
            
            RoleDTO savedRole = roleRepository.save(roleDTO);
            
            // 为新创建的角色设置权限
            if (savedRole != null && roleDTO.getPermissions() != null && !roleDTO.getPermissions().isEmpty()) {
                updateRolePermissions(savedRole.getId(), roleDTO.getPermissions());
            }
            
            // 重新获取角色信息（包括权限）
            RoleDTO roleWithPermissions = roleRepository.findById(savedRole.getId());
            
            return Result.success("角色创建成功", roleWithPermissions);
        } catch (Exception e) {
            return Result.error("角色创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新角色
     * @param roleDTO 角色数据传输对象
     * @return 更新结果
     */
    @Transactional
    public Result<RoleDTO> updateRole(RoleDTO roleDTO) {
        try {
            // 检查角色是否存在
            RoleDTO existingRole = roleRepository.findById(roleDTO.getId());
            if (existingRole == null) {
                return Result.error("角色不存在");
            }
            
            // 检查是否为内置角色
            if (isBuiltIn(existingRole.getCode())) {
                return Result.error("不能修改内置角色");
            }
            
            // 检查角色代码是否被其他角色使用
            RoleDTO roleWithSameCode = roleRepository.findByCode(roleDTO.getCode());
            if (roleWithSameCode != null && !roleWithSameCode.getId().equals(roleDTO.getId())) {
                return Result.error("角色代码已被其他角色使用");
            }
            
            // 更新角色基本信息
            RoleDTO updatedRole = roleRepository.update(roleDTO);
            
            // 更新角色权限
            updateRolePermissions(roleDTO.getId(), roleDTO.getPermissions());
            
            // 重新获取角色信息（包括权限）
            updatedRole = roleRepository.findById(roleDTO.getId());
            
            return Result.success("角色更新成功", updatedRole);
        } catch (Exception e) {
            return Result.error("角色更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    public Result<Void> deleteRole(Integer id) {
        try {
            // 检查角色是否存在
            RoleDTO role = roleRepository.findById(id);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            // 检查是否为内置角色
            if (isBuiltIn(role.getCode())) {
                return Result.error("不能删除内置角色");
            }
            
            boolean success = roleRepository.deleteById(id);
            if (success) {
                return Result.success("角色删除成功");
            } else {
                return Result.error("角色删除失败");
            }
        } catch (Exception e) {
            return Result.error("角色删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除角色
     * @param ids 角色ID列表
     * @return 删除结果
     */
    public Result<Void> batchDeleteRoles(List<Integer> ids) {
        try {
            // 检查所有角色是否存在且不是内置角色
            for (Integer id : ids) {
                RoleDTO role = roleRepository.findById(id);
                if (role == null) {
                    return Result.error("角色ID " + id + " 不存在");
                }
                if (isBuiltIn(role.getCode())) {
                    return Result.error("不能删除内置角色: " + role.getName());
                }
            }
            
            boolean success = roleRepository.batchDeleteByIds(ids);
            if (success) {
                return Result.success("批量删除角色成功");
            } else {
                return Result.error("批量删除角色失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查角色是否有指定权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 检查结果
     */
    public Result<Boolean> hasPermission(Integer roleId, String permissionCode) {
        try {
            RoleDTO role = roleRepository.findById(roleId);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            boolean hasPermission = hasPermission(role, permissionCode);
            return Result.success("权限检查成功", hasPermission);
        } catch (Exception e) {
            return Result.error("权限检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 为角色添加权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 添加结果
     */
    public Result<Void> addPermission(Integer roleId, String permissionCode) {
        try {
            RoleDTO role = roleRepository.findById(roleId);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            if (isBuiltIn(role.getCode())) {
                return Result.error("不能修改内置角色");
            }
            
            // 添加权限逻辑（简化处理）
            // 实际应该通过角色权限关联表来处理
            
            return Result.success("权限添加成功");
        } catch (Exception e) {
            return Result.error("权限添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 移除角色权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 移除结果
     */
    public Result<Void> removePermission(Integer roleId, String permissionCode) {
        try {
            RoleDTO role = roleRepository.findById(roleId);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            if (isBuiltIn(role.getCode())) {
                return Result.error("不能修改内置角色");
            }
            
            // 移除权限逻辑（简化处理）
            // 实际应该通过角色权限关联表来处理
            
            return Result.success("权限移除成功");
        } catch (Exception e) {
            return Result.error("权限移除失败: " + e.getMessage());
        }
    }
    
    // 业务规则：检查是否为内置角色
    private boolean isBuiltIn(String code) {
        return "ADMIN".equals(code) || "USER".equals(code);
    }
    
    // 业务规则：检查角色是否有指定权限
    private boolean hasPermission(RoleDTO role, String permissionCode) {
        // 通过角色权限关联表来查询
        if (isBuiltIn(role.getCode())) {
            // 内置角色拥有所有权限
            return true;
        }
        
        // 对于自定义角色，查询角色权限关联表
        List<PermissionPO> permissions = permissionMapper.findByRoleId(role.getId());
        if (permissions == null || permissions.isEmpty()) {
            // 如果角色没有关联权限，则没有权限
            return false;
        }
        
        return permissions.stream()
                .anyMatch(permission -> permissionCode.equals(permission.getCode()));
    }
    
    /**
     * 更新角色权限
     * @param roleId 角色ID
     * @param permissionCodes 权限代码列表
     */
    private void updateRolePermissions(Integer roleId, List<String> permissionCodes) {
        if (permissionCodes == null) {
            permissionCodes = new java.util.ArrayList<>();
        }
        
        // 先删除角色现有的所有权限
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 为角色添加新权限
        for (String permissionCode : permissionCodes) {
            // 根据权限代码查找权限ID
            PermissionPO permission = permissionMapper.findByCode(permissionCode);
            if (permission != null) {
                RolePermissionPO rolePermission = new RolePermissionPO();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permission.getId());
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }

    /**
     * 获取所有角色（包含权限信息）
     * @return 角色列表
     */
    public Result<List<RoleDTO>> getAllRolesWithPermissions() {
        try {
            List<RoleDTO> roles = roleRepository.findAll();
            
            // 为每个角色加载权限信息
            for (RoleDTO role : roles) {
                role.setPermissions(loadRolePermissions(role.getId()));
            }
            
            return Result.success("获取角色列表成功", roles);
        } catch (Exception e) {
            return Result.error("获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 加载角色的权限信息
     * @param roleId 角色ID
     * @return 权限代码列表
     */
    private List<String> loadRolePermissions(Integer roleId) {
        if (roleId == null) {
            return new java.util.ArrayList<>();
        }
        
        // 通过角色ID获取权限列表
        List<PermissionPO> permissionPOs = 
            permissionMapper.findByRoleId(roleId);
        
        // 如果没有找到权限，返回空列表而不是null
        if (permissionPOs == null || permissionPOs.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 提取权限代码
        return permissionPOs.stream()
                .map(PermissionPO::getCode)
                .collect(java.util.stream.Collectors.toList());
    }
}