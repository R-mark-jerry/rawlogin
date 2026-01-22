package com.rawlogin.api;

import com.rawlogin.common.Result;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import com.rawlogin.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 前后端分离版本
 * 只处理登录和注册功能
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest) {
        logger.info("用户登录尝试: {}", loginRequest.getUsername());
        
        // 调用服务层
        Result<User> result = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            // 登录成功，生成JWT令牌
            User user = result.getData();
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            
            // 清除密码信息，不返回给前端
            user.setPassword(null);
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            
            logger.info("用户登录成功: {}", user.getUsername());
            return ResponseEntity.ok(Result.success("登录成功", data));
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
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<Result<User>> getCurrentUser(HttpServletRequest request) {
        try {
            // 从请求属性中获取用户信息（由JWT拦截器设置）
            String username = (String) request.getAttribute("username");
            Integer userId = (Integer) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            
            if (username != null) {
                // 获取完整用户信息
                Result<User> userResult = userService.findById(userId);
                if (userResult.isSuccess()) {
                    User user = userResult.getData();
                    user.setPassword(null); // 清除密码信息
                    return ResponseEntity.ok(Result.success("获取用户信息成功", user));
                }
            }
            
            return ResponseEntity.status(401).body(Result.error("用户未登录"));
        } catch (Exception e) {
            logger.error("获取当前用户信息时发生异常", e);
            return ResponseEntity.status(500).body(Result.error("系统错误，请稍后再试"));
        }
    }
    
    /**
     * 用户登出
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(HttpServletRequest request) {
        try {
            // 从请求头中获取令牌
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                
                // 从令牌中获取用户信息
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null) {
                    logger.info("用户登出: {}", username);
                    
                    // 使令牌失效
                    jwtUtil.invalidateToken(token);
                    
                    return ResponseEntity.ok(Result.success("登出成功"));
                }
            }
            
            // 如果没有有效的令牌，仍然返回成功，因为用户已经处于未登录状态
            logger.info("用户登出（无有效令牌）");
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