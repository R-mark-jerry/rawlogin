package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil {
    private static final Logger logger = Logger.getLogger(DBUtil.class.getName());
    private static Properties props = new Properties();
    private static boolean initialized = false;

    // 静态代码块，加载数据库配置
    static {
        initialize();
    }

    /**
     * 初始化数据库连接配置
     */
    private static void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("database.properties");
            if (input == null) {
                logger.severe("无法找到database.properties配置文件");
                throw new RuntimeException("数据库配置文件未找到");
            }
            
            props.load(input);
            String driver = props.getProperty("db.driver");
            if (driver != null) {
                Class.forName(driver);
                logger.info("数据库驱动加载成功: " + driver);
            } else {
                logger.severe("数据库驱动未配置");
                throw new RuntimeException("数据库驱动未配置");
            }
            
            initialized = true;
            logger.info("数据库配置初始化成功");
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "数据库配置初始化失败", e);
            throw new RuntimeException("数据库配置初始化失败", e);
        }
    }

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     * @throws SQLException 如果连接失败
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        try {
            Connection conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.username"),
                    props.getProperty("db.password")
            );
            
            // 设置连接参数，提高可靠性
            conn.setAutoCommit(true);
            logger.fine("数据库连接创建成功");
            return conn;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "创建数据库连接失败", e);
            throw e;
        }
    }

    /**
     * 安全关闭数据库资源
     * @param conn 数据库连接
     * @param stmt SQL语句对象
     * @param rs 结果集对象
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        // 关闭结果集
        closeQuietly(rs, "ResultSet");
        
        // 关闭语句对象
        closeQuietly(stmt, "Statement");
        
        // 关闭连接
        closeQuietly(conn, "Connection");
    }
    
    /**
     * 安静地关闭资源，不抛出异常
     * @param resource 要关闭的资源
     * @param resourceType 资源类型（用于日志）
     */
    private static void closeQuietly(AutoCloseable resource, String resourceType) {
        if (resource != null) {
            try {
                resource.close();
                logger.fine(resourceType + " 已关闭");
            } catch (Exception e) {
                logger.log(Level.WARNING, "关闭 " + resourceType + " 时发生异常", e);
            }
        }
    }
    
    /**
     * 测试数据库连接
     * @return 连接是否成功
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "数据库连接测试失败", e);
            return false;
        } finally {
            close(conn, null, null);
        }
    }
}
