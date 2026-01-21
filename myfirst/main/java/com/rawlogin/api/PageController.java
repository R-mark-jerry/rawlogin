package com.rawlogin.api;

import com.rawlogin.common.Result;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 页面控制器
 * 简化版的页面控制器，使用common、service、api分层架构
 * 所有接口统一返回Result类型
 */
@RestController
@RequestMapping("")
public class PageController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取首页信息
     * @param session HTTP会话
     * @return 首页信息
     */
    @GetMapping("/")
    public ResponseEntity<Result<String>> index(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(Result.success("已登录用户访问首页", "index"));
        } else {
            return ResponseEntity.ok(Result.success("未登录用户访问首页", "index"));
        }
    }
    
    /**
     * 获取登录页面信息
     * @param session HTTP会话
     * @return 登录页面信息
     */
    @GetMapping("/login")
    public ResponseEntity<Result<String>> loginPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(Result.success("用户已登录", "welcome"));
        } else {
            return ResponseEntity.ok(Result.success("显示登录页面", "index"));
        }
    }
    
    /**
     * 获取欢迎页面信息
     * @param session HTTP会话
     * @return 欢迎页面信息
     */
    @GetMapping("/welcome")
    public ResponseEntity<Result<User>> welcome(HttpSession session) {
        // 检查用户是否已登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Result.error(401, "用户未登录，请先登录"));
        }
        
        return ResponseEntity.ok(Result.success("获取用户信息成功", user));
    }
    
    /**
     * 获取注册页面信息
     * @return 注册页面信息
     */
    @GetMapping("/register")
    public ResponseEntity<Result<String>> registerPage() {
        return ResponseEntity.ok(Result.success("显示注册页面", "register"));
    }
    
    /**
     * 处理注册请求
     * @param registerRequest 注册请求
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Result<User>> handleRegister(@ModelAttribute RegisterRequest registerRequest, HttpSession session) {
        // 创建用户对象
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setRealName(registerRequest.getRealName());
        
        // 调用服务层
        Result<User> result = userService.register(user);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取登录成功页面信息
     * @return 登录成功页面信息
     */
    @GetMapping("/login-success")
    public ResponseEntity<Result<String>> loginSuccessPage() {
        return ResponseEntity.ok(Result.success("显示登录成功页面", "login-success"));
    }
    
    /**
     * 处理登出请求
     * @param session HTTP会话
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 登出结果
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<Result<Void>> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // 清除记住用户名的Cookie
                if (request.getCookies() != null) {
                    for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                        if ("username".equals(cookie.getName())) {
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                            break;
                        }
                    }
                }
            }
            
            // 使会话失效
            session.invalidate();
            
            return ResponseEntity.ok(Result.success("登出成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Result.error("登出过程中发生异常"));
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