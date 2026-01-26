package com.rawlogin.domain.repository;

import com.rawlogin.application.dto.RoleDTO;

import java.util.List;

/**
 * 角色仓储接口
 * 定义角色相关的领域服务接口
 */
public interface RoleRepository {
    
    /**
     * 获取所有角色
     * @return 角色列表
     */
    List<RoleDTO> findAll();
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色数据传输对象
     */
    RoleDTO findById(Integer id);
    
    /**
     * 根据角色代码获取角色
     * @param code 角色代码
     * @return 角色数据传输对象
     */
    RoleDTO findByCode(String code);
    
    /**
     * 根据条件搜索角色
     * @param name 角色名称（可选）
     * @param code 角色代码（可选）
     * @param status 角色状态（可选）
     * @param builtIn 是否内置角色（可选）
     * @return 角色列表
     */
    List<RoleDTO> searchRoles(String name, String code, Integer status, Boolean builtIn);
    
    /**
     * 保存角色
     * @param roleDTO 角色数据传输对象
     * @return 保存后的角色数据传输对象
     */
    RoleDTO save(RoleDTO roleDTO);
    
    /**
     * 更新角色
     * @param roleDTO 角色数据传输对象
     * @return 更新后的角色数据传输对象
     */
    RoleDTO update(RoleDTO roleDTO);
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 是否删除成功
     */
    boolean deleteById(Integer id);
    
    /**
     * 批量删除角色
     * @param ids 角色ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteByIds(List<Integer> ids);
    
    /**
     * 根据用户ID查找角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleDTO> findRolesByUserId(Integer userId);
}