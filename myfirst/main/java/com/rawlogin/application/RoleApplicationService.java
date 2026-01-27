package com.rawlogin.application;

import com.rawlogin.common.Result;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.interfaces.vo.RoleVO;

import java.util.List;

/**
 * 角色应用服务接口
 * 定义角色管理的应用层服务接口
 */
public interface RoleApplicationService {
    
    /**
     * 获取所有角色
     * @return 角色列表
     */
    Result<List<RoleVO>> getAllRoles();
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    Result<RoleVO> getRoleById(Integer id);
    
    /**
     * 根据条件搜索角色
     * @param name 角色名称（可选）
     * @param code 角色代码（可选）
     * @param status 角色状态（可选）
     * @param builtIn 是否内置角色（可选）
     * @return 角色列表
     */
    Result<List<RoleVO>> searchRoles(String name, String code, Integer status, Boolean builtIn);
    
    /**
     * 创建角色
     * @param roleDTO 角色数据传输对象
     * @return 创建结果
     */
    Result<RoleVO> createRole(RoleDTO roleDTO);
    
    /**
     * 更新角色
     * @param roleDTO 角色数据传输对象
     * @return 更新结果
     */
    Result<RoleVO> updateRole(RoleDTO roleDTO);
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    Result<Void> deleteRole(Integer id);
    
    /**
     * 批量删除角色
     * @param ids 角色ID列表
     * @return 删除结果
     */
    Result<Void> batchDeleteRoles(List<Integer> ids);
    
    /**
     * 检查角色是否有指定权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 检查结果
     */
    Result<Boolean> hasPermission(Integer roleId, String permissionCode);
    
    /**
     * 为角色添加权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 添加结果
     */
    Result<Void> addPermission(Integer roleId, String permissionCode);
    
    /**
     * 移除角色权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 移除结果
     */
    Result<Void> removePermission(Integer roleId, String permissionCode);
    
    /**
     * 获取所有角色（包含权限信息）
     * @return 角色列表
     */
    Result<List<RoleVO>> getAllRolesWithPermissions();
}