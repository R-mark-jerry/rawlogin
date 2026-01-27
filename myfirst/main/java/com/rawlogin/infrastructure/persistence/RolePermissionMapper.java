package com.rawlogin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rawlogin.infrastructure.po.RolePermissionPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色权限关联数据访问接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionPO> {
    
    /**
     * 根据角色ID删除所有权限关联
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Integer roleId);
    
    /**
     * 根据角色ID和权限ID删除权限关联
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int deleteByRoleIdAndPermissionId(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
}