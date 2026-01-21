package com.rawlogin.api;

import com.rawlogin.common.Result;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 认证控制器 - 前后端分离版本
 * 只处理登录和注册功能
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @param session HTTP会话
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<Result<User>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        logger.info("用户登录尝试: {}", loginRequest.getUsername());
        
        // 调用服务层
        Result<User> result = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            // 登录成功，将用户信息存储到会话中
            User user = result.getData();
            session.setAttribute("user", user);
            logger.info("用户登录成功: {}", user.getUsername());
            return ResponseEntity.ok(Result.success("登录成功", user));
        } else {
            // 登录失败
            logger.warn("登录失败: {}", result.getMessage());
            return ResponseEntity.status(401).body(Result.error(result.getMessage()));
        }
    }
    
    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Result<User>> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("用户注册尝试: {}", registerRequest.getUsername());
        
        // 创建用户对象
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setRealName(registerRequest.getRealName());
        
        // 调用服务层
        Result<User> result = userService.register(user);
        
        if (result.isSuccess()) {
            logger.info("用户注册成功: {}", registerRequest.getUsername());
            return ResponseEntity.ok(Result.success("注册成功", result.getData()));
        } else {
            logger.warn("用户注册失败: {} - {}", registerRequest.getUsername(), result.getMessage());
            return ResponseEntity.status(400).body(Result.error(result.getMessage()));
        }
    }
    
    /**
     * 获取当前用户信息
     * @param session HTTP会话
     * @return 用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<Result<User>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        if (user != null) {
            return ResponseEntity.ok(Result.success("获取用户信息成功", user));
        } else {
            return ResponseEntity.status(401).body(Result.error("用户未登录"));
        }
    }
    
    /**
     * 用户登出
     * @param session HTTP会话
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                logger.info("用户登出: {}", user.getUsername());
            }
            
            // 使会话失效
            session.invalidate();
            
            return ResponseEntity.ok(Result.success("登出成功"));
        } catch (Exception e) {
            logger.error("登出过程中发生异常", e);
            return ResponseEntity.status(500).body(Result.error("系统错误，请稍后再试"));
        }
    }
    
    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    /**
     * 注册请求DTO
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String realName;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRealName() {
            return realName;
        }
        
        public void setRealName(String realName) {
            this.realName = realName;
        }
    }
}