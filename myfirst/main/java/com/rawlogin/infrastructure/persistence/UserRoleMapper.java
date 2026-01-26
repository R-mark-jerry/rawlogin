package com.rawlogin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rawlogin.infrastructure.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 用户角色关联数据访问接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePO> {
    
    /**
     * 根据用户ID删除所有用户角色关联
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
    
    /**
     * 根据用户ID和角色ID删除用户角色关联
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteByUserIdAndRoleId(Integer userId, Integer roleId);
    
    /**
     * 根据角色ID获取所有用户ID
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM user_roles WHERE role_id = #{roleId}")
    List<Integer> selectUserIdsByRoleId(Integer roleId);
    
    /**
     * 根据用户ID获取所有角色ID
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM user_roles WHERE user_id = #{userId}")
    List<Integer> selectRoleIdsByUserId(Integer userId);
}