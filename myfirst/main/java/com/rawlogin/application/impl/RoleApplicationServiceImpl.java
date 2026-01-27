package com.rawlogin.application.impl;

import com.rawlogin.application.RoleApplicationService;
import com.rawlogin.common.Result;
import com.rawlogin.common.ResultCode;
import com.rawlogin.domain.service.RoleDomainService;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.interfaces.vo.RoleVO;
import com.rawlogin.application.converter.RoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色应用服务实现类
 * 实现角色管理的应用层服务
 */
@Service
public class RoleApplicationServiceImpl implements RoleApplicationService {
    
    @Autowired
    private RoleDomainService roleDomainService;
    
    @Override
    public Result<List<RoleVO>> getAllRoles() {
        try {
            Result<List<RoleDTO>> result = roleDomainService.getAllRoles();
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            List<RoleVO> roleVOs = RoleConverter.toVOList(result.getData());
            
            return Result.success(result.getMessage(), roleVOs);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "获取角色列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<RoleVO> getRoleById(Integer id) {
        try {
            Result<RoleDTO> result = roleDomainService.getRoleById(id);
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            RoleVO roleVO = RoleConverter.toVO(result.getData());
            return Result.success(result.getMessage(), roleVO);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "获取角色信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<RoleVO>> searchRoles(String name, String code, Integer status, Boolean builtIn) {
        try {
            Result<List<RoleDTO>> result = roleDomainService.searchRoles(name, code, status, builtIn);
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            List<RoleVO> roleVOs = RoleConverter.toVOList(result.getData());
            
            return Result.success(result.getMessage(), roleVOs);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "搜索角色失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<RoleVO> createRole(RoleDTO roleDTO) {
        try {
            // 验证必填字段
            if (roleDTO.getName() == null || roleDTO.getName().trim().isEmpty()) {
                return Result.error(ResultCode.PARAM_ERROR, "角色名称不能为空");
            }
            if (roleDTO.getCode() == null || roleDTO.getCode().trim().isEmpty()) {
                return Result.error(ResultCode.PARAM_ERROR, "角色代码不能为空");
            }
            
            // 调用领域服务创建角色
            Result<RoleDTO> result = roleDomainService.createRole(roleDTO);
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            // 转换为VO
            RoleVO roleVO = RoleConverter.toVO(result.getData());
            return Result.success(result.getMessage(), roleVO);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "创建角色失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<RoleVO> updateRole(RoleDTO roleDTO) {
        try {
            // 验证必填字段
            if (roleDTO.getName() == null || roleDTO.getName().trim().isEmpty()) {
                return Result.error(ResultCode.PARAM_ERROR, "角色名称不能为空");
            }
            if (roleDTO.getCode() == null || roleDTO.getCode().trim().isEmpty()) {
                return Result.error(ResultCode.PARAM_ERROR, "角色代码不能为空");
            }
            
            // 调用领域服务更新角色
            Result<RoleDTO> result = roleDomainService.updateRole(roleDTO);
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            // 转换为VO
            RoleVO roleVO = RoleConverter.toVO(result.getData());
            return Result.success(result.getMessage(), roleVO);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "更新角色失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> deleteRole(Integer id) {
        try {
            // 调用领域服务删除角色
            return roleDomainService.deleteRole(id);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "删除角色失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> batchDeleteRoles(List<Integer> ids) {
        try {
            // 调用领域服务批量删除角色
            return roleDomainService.batchDeleteRoles(ids);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "批量删除角色失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Boolean> hasPermission(Integer roleId, String permissionCode) {
        try {
            // 调用领域服务检查权限
            return roleDomainService.hasPermission(roleId, permissionCode);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "权限检查失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> addPermission(Integer roleId, String permissionCode) {
        try {
            // 调用领域服务添加权限
            return roleDomainService.addPermission(roleId, permissionCode);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "添加权限失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> removePermission(Integer roleId, String permissionCode) {
        try {
            // 调用领域服务移除权限
            return roleDomainService.removePermission(roleId, permissionCode);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "移除权限失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<RoleVO>> getAllRolesWithPermissions() {
        try {
            Result<List<RoleDTO>> result = roleDomainService.getAllRolesWithPermissions();
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }
            
            List<RoleVO> roleVOs = RoleConverter.toVOList(result.getData());
            
            return Result.success(result.getMessage(), roleVOs);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "获取角色列表失败: " + e.getMessage());
        }
    }
}