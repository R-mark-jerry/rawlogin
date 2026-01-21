package com.rawlogin.api;

import com.rawlogin.common.Result;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户API控制器
 * 简化版的控制器，使用common、service、api分层架构
 */
@Controller
@RequestMapping("/api")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 处理AJAX登录请求
     * @param loginRequest 登录请求
     * @param session HTTP会话
     * @param response HTTP响应
     * @return JSON响应
     */
    @PostMapping("/ajax-login")
    @ResponseBody
    public ResponseEntity<Result<User>> ajaxLogin(@ModelAttribute LoginRequest loginRequest,
                                            HttpSession session,
                                            HttpServletResponse response) {
        
        logger.info("AJAX用户登录尝试: {}", loginRequest.getUsername());
        
        // 调用服务层
        Result<User> result = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            // 登录成功
            User user = result.getData();
            
            // 将用户信息存储到会话中
            session.setAttribute("user", user);
            
            // 如果选择记住用户名，设置Cookie
            if (loginRequest.isRemember()) {
                Cookie cookie = new Cookie("username", loginRequest.getUsername());
                cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
                cookie.setPath("/");
                response.addCookie(cookie);
                logger.info("设置记住用户名Cookie: {}", loginRequest.getUsername());
            }
            
            // 返回成功响应
            return ResponseEntity.ok(Result.success("登录成功", user));
        } else {
            // 登录失败
            logger.warn("登录失败: {}", result.getMessage());
            return ResponseEntity.status(401).body(Result.error(401, result.getMessage()));
        }
    }
    
    /**
     * 处理登录API请求
     * @param loginRequest 登录请求
     * @param session HTTP会话
     * @return 登录结果
     */
    @PostMapping("/user/login")
    @ResponseBody
    public ResponseEntity<Result<User>> loginApi(@ModelAttribute LoginRequest loginRequest, HttpSession session) {
        logger.info("前端登录API请求: {}", loginRequest.getUsername());
        
        // 调用服务层
        Result<User> result = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            // 登录成功
            User user = result.getData();
            
            // 将用户信息存储到会话中
            session.setAttribute("user", user);
            
            // 返回成功响应
            return ResponseEntity.ok(Result.success("登录成功", user));
        } else {
            // 登录失败
            logger.warn("登录失败: {}", result.getMessage());
            return ResponseEntity.ok(Result.error(result.getMessage()));
        }
    }
    
    /**
     * 获取当前用户信息API
     * @param session HTTP会话
     * @return 用户信息
     */
    @GetMapping("/user/current")
    @ResponseBody
    public ResponseEntity<?> getCurrentUserApi(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        if (user != null) {
            return ResponseEntity.ok(Result.success("获取用户信息成功", user));
        } else {
            return ResponseEntity.status(401).body(Result.error(401, "用户未登录"));
        }
    }
    
    /**
     * 处理登出API请求
     * @param session HTTP会话
     * @return 登出结果
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ResponseEntity<Result<Void>> logoutApi(HttpSession session) {
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
     * 处理用户注册请求
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/user/register")
    @ResponseBody
    public ResponseEntity<Result<User>> register(@ModelAttribute RegisterRequest registerRequest) {
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
            return ResponseEntity.ok(Result.error(result.getMessage()));
        }
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/user/username/{username}")
    @ResponseBody
    public ResponseEntity<Result<User>> findByUsername(@PathVariable String username) {
        Result<User> result = userService.findByUsername(username);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(404).body(result);
        }
    }
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<Result<User>> findById(@PathVariable Integer id) {
        Result<User> result = userService.findById(id);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(404).body(result);
        }
    }
    
    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;
        private boolean remember;
        
        // Getters and Setters
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
        
        public boolean isRemember() {
            return remember;
        }
        
        public void setRemember(boolean remember) {
            this.remember = remember;
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
        
        // Getters and Setters
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