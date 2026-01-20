package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 * 使用Spring Data JPA实现数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 根据用户名和状态查找用户
     * @param username 用户名
     * @param status 用户状态
     * @return 用户对象（可选）
     */
    Optional<User> findByUsernameAndStatus(String username, Integer status);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（可选）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 更新用户最后登录时间
     * @param username 用户名
     * @return 影响的行数
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = CURRENT_TIMESTAMP WHERE u.username = :username")
    int updateLastLoginTime(@Param("username") String username);
}