package com.rawlogin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rawlogin.infrastructure.po.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 角色数据访问接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {
    
    /**
     * 根据角色代码查询角色
     * @param roleCode 角色代码
     * @return 角色实体
     */
    @Select("SELECT * FROM roles WHERE code = #{roleCode}")
    RolePO findByCode(@Param("roleCode") String roleCode);
    
    /**
     * 查询所有启用的角色
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE status = 1 ORDER BY id")
    List<RolePO> findAllEnabled();
    
    /**
     * 根据条件搜索角色
     * @param name 角色名称（可选）
     * @param code 角色代码（可选）
     * @param status 角色状态（可选）
     * @param builtIn 是否内置角色（可选）
     * @return 角色列表
     */
    @Select("<script>" +
            "SELECT * FROM roles WHERE 1=1 " +
            "<if test='name != null and name != \"\"'>" +
            "AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='code != null and code != \"\"'>" +
            "AND code LIKE CONCAT('%', #{code}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "<if test='builtIn != null'>" +
            "AND built_in = #{builtIn} " +
            "</if>" +
            "ORDER BY id" +
            "</script>")
    List<RolePO> searchRoles(@Param("name") String name, 
                           @Param("code") String code, 
                           @Param("status") Integer status, 
                           @Param("builtIn") Boolean builtIn);
    
    /**
     * 批量删除角色
     * @param ids 角色ID列表
     * @return 删除的记录数
     */
    @Delete("<script>" +
            "DELETE FROM roles WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDeleteByIds(@Param("ids") List<Integer> ids);
    
    /**
     * 根据用户ID查询用户角色
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<RolePO> findByUserId(@Param("userId") Integer userId);
}