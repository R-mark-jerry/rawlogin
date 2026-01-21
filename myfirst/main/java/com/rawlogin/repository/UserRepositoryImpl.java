package com.rawlogin.repository;

import com.rawlogin.common.Result;
import com.rawlogin.service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问实现类
 * 实现用户相关的数据库操作
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 用户行映射器
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRealName(rs.getString("real_name"));
        
        Timestamp createTimeTimestamp = rs.getTimestamp("create_time");
        if (createTimeTimestamp != null) {
            user.setCreateTime(createTimeTimestamp.toLocalDateTime());
        }
        
        Timestamp lastLoginTimeTimestamp = rs.getTimestamp("last_login_time");
        if (lastLoginTimeTimestamp != null) {
            user.setLastLoginTime(lastLoginTimeTimestamp.toLocalDateTime());
        }
        
        user.setStatus(rs.getInt("status"));
        return user;
    };
    
    @Override
    public Result<User> save(User user) {
        try {
            String sql = "INSERT INTO users (username, password, email, real_name, create_time, status) VALUES (?, ?, ?, ?, ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            int rows = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getRealName());
                ps.setTimestamp(5, Timestamp.valueOf(user.getCreateTime()));
                ps.setInt(6, user.getStatus());
                return ps;
            }, keyHolder);
            
            if (rows > 0) {
                // 获取生成的ID
                Integer generatedId = keyHolder.getKey().intValue();
                user.setId(generatedId);
                
                logger.info("用户保存成功: {}", user.getUsername());
                return Result.success("用户保存成功", user);
            } else {
                logger.warn("用户保存失败: {}", user.getUsername());
                return Result.error("用户保存失败");
            }
        } catch (Exception e) {
            logger.error("保存用户时发生异常", e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> findByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
            
            if (users.isEmpty()) {
                logger.warn("未找到用户: {}", username);
                return Result.error("用户不存在");
            }
            
            User user = users.get(0);
            logger.info("找到用户: {}", username);
            return Result.success("找到用户", user);
        } catch (Exception e) {
            logger.error("根据用户名查找用户时发生异常: {}", username, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<User> findById(Integer id) {
        try {
            String sql = "SELECT * FROM users WHERE id = ?";
            List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
            
            if (users.isEmpty()) {
                logger.warn("未找到用户: {}", id);
                return Result.error("用户不存在");
            }
            
            User user = users.get(0);
            logger.info("找到用户: {}", id);
            return Result.success("找到用户", user);
        } catch (Exception e) {
            logger.error("根据ID查找用户时发生异常: {}", id, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public Result<Void> updateLastLoginTime(Integer userId) {
        try {
            String sql = "UPDATE users SET last_login_time = ? WHERE id = ?";
            int rows = jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()), userId);
            
            if (rows > 0) {
                logger.info("更新用户最后登录时间成功: {}", userId);
                return Result.success("更新成功");
            } else {
                logger.warn("更新用户最后登录时间失败，用户不存在: {}", userId);
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            logger.error("更新用户最后登录时间时发生异常: {}", userId, e);
            return Result.error("系统错误，请稍后再试");
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("检查用户名是否存在时发生异常: {}", username, e);
            return false;
        }
    }
}