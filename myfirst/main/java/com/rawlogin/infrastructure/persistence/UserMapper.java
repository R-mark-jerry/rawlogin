package com.rawlogin.infrastructure.persistence;

import com.rawlogin.domain.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户持久化映射器
 * 桥接领域模型和数据库操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) FROM users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     * @param username 用户名
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) FROM users WHERE username = #{username} AND id != #{excludeId}")
    boolean existsByUsernameAndExcludeId(@Param("username") String username, @Param("excludeId") Integer excludeId);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Integer userId);
    
    /**
     * 根据条件查询用户
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param status 状态（可选）
     * @param role 角色（可选）
     * @return 用户列表
     */
    @Select("<script>" +
            "SELECT * FROM users WHERE 1=1 " +
            "<if test='username != null and username != \"\"'>" +
            "AND username LIKE CONCAT('%', #{username}, '%') " +
            "</if>" +
            "<if test='email != null and email != \"\"'>" +
            "AND email LIKE CONCAT('%', #{email}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "<if test='role != null and role != \"\"'>" +
            "AND role = #{role} " +
            "</if>" +
            "ORDER BY id DESC" +
            "</script>")
    List<User> selectByCondition(
            @Param("username") String username,
            @Param("email") String email,
            @Param("status") Integer status,
            @Param("role") String role
    );
}