package org.example.service.impl;

import org.example.model.User;
import org.example.service.UserService;
import org.example.util.DBUtil;
import org.example.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public User login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            // 获取数据库连接
            conn = DBUtil.getConnection();

            // SQL查询语句 - 先获取用户信息
            String sql = "SELECT id, username, password, email, real_name, create_time, last_login_time, status " +
                    "FROM users WHERE username = ? AND status = 1";

            // 创建预处理语句
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            // 执行查询
            rs = pstmt.executeQuery();
            
            // 处理结果集
            if (rs.next()) {
                // 验证密码
                String encryptedPassword = rs.getString("password");
                if (PasswordUtil.verify(password, encryptedPassword)) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    // 不设置密码到User对象中，避免密码泄露
                    user.setEmail(rs.getString("email"));
                    user.setRealName(rs.getString("real_name"));
                    user.setCreateTime(rs.getString("create_time"));
                    user.setLastLoginTime(rs.getString("last_login_time"));
                    user.setStatus(rs.getInt("status"));

                    // 更新最后登录时间
                    updateLastLoginTime(username);
                }
            }


        } catch (SQLException e) {
            logger.log(Level.SEVERE, "用户登录过程中发生数据库异常: " + username, e);
        } finally {
            // 关闭资源
            DBUtil.close(conn, pstmt, rs);
        }

        return user;
    }

    /**
     * 更新用户最后登录时间
     * @param username 用户名
     */
    private void updateLastLoginTime(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE users SET last_login_time = NOW() WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "更新用户最后登录时间失败: " + username, e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, username, password, email, real_name, create_time, last_login_time, status " +
                    "FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                // 不设置密码到User对象中，避免密码泄露
                user.setEmail(rs.getString("email"));
                user.setRealName(rs.getString("real_name"));
                user.setCreateTime(rs.getString("create_time"));
                user.setLastLoginTime(rs.getString("last_login_time"));
                user.setStatus(rs.getInt("status"));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "根据用户名查找用户失败: " + username, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return user;
    }

    /**
     * 注册新用户
     * @param user 用户对象
     * @return 注册成功返回true，失败返回false
     */
    public boolean register(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO users (username, password, email, real_name, create_time, status) " +
                    "VALUES (?, ?, ?, ?, NOW(), 1)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, PasswordUtil.encrypt(user.getPassword())); // 加密密码
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRealName());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                result = true;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "用户注册失败: " + user.getUsername(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }

        return result;
    }
}
