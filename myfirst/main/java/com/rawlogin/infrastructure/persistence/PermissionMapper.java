package com.rawlogin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rawlogin.infrastructure.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限数据访问接口
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionPO> {
    
    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<PermissionPO> findByRoleId(@Param("roleId") Integer roleId);
    
    /**
     * 根据权限代码查询权限
     * @param permissionCode 权限代码
     * @return 权限持久化对象
     */
    @Select("SELECT * FROM permissions WHERE code = #{permissionCode}")
    PermissionPO findByCode(@Param("permissionCode") String permissionCode);
    
    /**
     * 查询所有权限
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions ORDER BY module, code")
    List<PermissionPO> findAll();
    
    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<PermissionPO> findByUserId(@Param("userId") Integer userId);
}