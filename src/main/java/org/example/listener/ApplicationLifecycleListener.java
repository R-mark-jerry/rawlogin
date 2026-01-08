package org.example.listener;

import org.example.util.DBUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 应用程序生命周期监听器
 * 负责在应用程序启动和关闭时执行必要的初始化和清理工作
 */
@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(ApplicationLifecycleListener.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("应用程序正在启动...");
        
        // 尝试加载日志配置
        try {
            InputStream logConfig = getClass().getClassLoader().getResourceAsStream("logging.properties");
            if (logConfig != null) {
                LogManager.getLogManager().readConfiguration(logConfig);
                logger.info("日志配置加载成功");
            } else {
                logger.warning("未找到日志配置文件，使用默认配置");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "加载日志配置文件失败", e);
        }
        
        // 测试数据库连接
        try {
            if (DBUtil.testConnection()) {
                logger.info("数据库连接测试成功");
            } else {
                logger.warning("数据库连接测试失败");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "数据库连接测试异常", e);
        }
        
        logger.info("应用程序启动完成");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("应用程序正在关闭...");
        
        // 执行清理工作
        try {
            // 这里可以添加其他需要清理的资源
            logger.info("资源清理完成");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "资源清理过程中发生异常", e);
        }
        
        logger.info("应用程序已关闭");
    }
}