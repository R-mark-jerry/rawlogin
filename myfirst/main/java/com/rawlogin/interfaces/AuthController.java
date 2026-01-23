package com.rawlogin.interfaces;

import com.rawlogin.application.UserApplicationService;
import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.interfaces.vo.UserVO;
import com.rawlogin.domain.model.User;
import com.rawlogin.util.JwtUtil;
import com.rawlogin.application.converter.UserConverter;
import com.rawlogin.common.Result;
import com.rawlogin.config.annotation.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口控制器
 * 处理登录和注册功能的接口层
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserApplicationService userApplicationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录接口
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest) {
        logger.info("用户登录尝试: {}", loginRequest.getUsername());
        
        // 调用应用服务层
        Result<UserDTO> result = userApplicationService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (result.isSuccess()) {
            // 登录成功，生成JWT令牌
            UserDTO userDTO = result.getData();
            String token = jwtUtil.generateToken(userDTO.getUsername(), userDTO.getId(), userDTO.getRole());
            
            // 转换为VO
            UserVO userVO = UserConverter.toVO(userDTO);
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userVO);
            
            logger.info("用户登录成功: {}", userDTO.getUsername());
            return ResponseEntity.ok(Result.success("登录成功", data));
        } else {
            // 登录失败
            logger.warn("登录失败: {}", result.getMessage());
            return ResponseEntity.status(401).body(Result.error(result.getMessage()));
        }
    }
    
    /**
     * 用户注册接口
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Result<UserVO>> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("用户注册尝试: {}", registerRequest.getUsername());
        
        // 创建用户对象
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        
        // 调用应用服务层
        Result<UserDTO> result = userApplicationService.register(user);
        
        if (result.isSuccess()) {
            // 转换为VO
            UserVO userVO = UserConverter.toVO(result.getData());
            
            logger.info("用户注册成功: {}", registerRequest.getUsername());
            return ResponseEntity.ok(Result.success("注册成功", userVO));
        } else {
            logger.warn("用户注册失败: {} - {}", registerRequest.getUsername(), result.getMessage());
            return ResponseEntity.status(400).body(Result.error(result.getMessage()));
        }
    }
    
    /**
     * 获取当前用户信息接口
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/current")
    @PreAuthorize(authenticated = true)
    public ResponseEntity<Result<UserVO>> getCurrentUser(HttpServletRequest request) {
        try {
            // 从请求属性中获取用户信息（由JWT拦截器设置）
            Integer userId = (Integer) request.getAttribute("userId");
            
            if (userId != null) {
                // 获取完整用户信息
                Result<UserDTO> userResult = userApplicationService.getCurrentUser(userId);
                if (userResult.isSuccess()) {
                    // 转换为VO
                    UserVO userVO = UserConverter.toVO(userResult.getData());
                    return ResponseEntity.ok(Result.success("获取用户信息成功", userVO));
                }
            }
            
            return ResponseEntity.status(401).body(Result.error("用户未登录"));
        } catch (Exception e) {
            logger.error("获取当前用户信息时发生异常", e);
            return ResponseEntity.status(500).body(Result.error("系统错误，请稍后再试"));
        }
    }
    
    /**
     * 用户登出接口
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    @PreAuthorize(authenticated = true)
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
    }
}