package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录控制器
 * 使用Spring MVC替代原始的Servlet
 */
@Controller
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private UserService userService;

    /**
     * 显示登录页面
     * @return 登录页面
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 处理登录请求
     * @param username 用户名
     * @param password 密码
     * @param remember 是否记住用户名
     * @param model 模型对象
     * @param session HTTP会话
     * @param response HTTP响应
     * @param redirectAttributes 重定向属性
     * @return 视图名称
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                      @RequestParam String password,
                      @RequestParam(required = false, defaultValue = "false") boolean remember,
                      Model model,
                      HttpSession session,
                      HttpServletResponse response) {
        
        logger.info("用户登录尝试: {}", username);

        // 验证输入参数
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            logger.warn("登录失败: 用户名或密码为空");
            model.addAttribute("error", "用户名和密码不能为空");
            return "index";
        }

        try {
            // 先检查用户是否存在
            User existingUser = userService.findByUsername(username);
            if (existingUser == null) {
                logger.warn("登录失败: 用户不存在 - {}", username);
                model.addAttribute("error", "用户名或密码错误");
                return "index";
            }
            
            // 验证密码
            boolean passwordMatch = false;
            try {
                logger.debug("尝试验证密码 - 输入密码: {}, 数据库密码: {}", password, existingUser.getPassword());
                passwordMatch = org.example.util.PasswordUtil.verify(password, existingUser.getPassword());
                logger.info("BCrypt密码验证结果: {}", passwordMatch);
            } catch (Exception e) {
                logger.error("密码验证异常: {}", e.getMessage(), e);
                // 如果BCrypt验证失败，尝试明文比较
                passwordMatch = password.equals(existingUser.getPassword());
                logger.info("明文密码比较结果: {}", passwordMatch);
            }
            
            if (passwordMatch) {
                // 登录成功
                logger.info("用户登录成功: {}", username);
                
                // 将用户信息存储到会话中
                session.setAttribute("user", existingUser);
                
                // 更新最后登录时间
                try {
                    // 使用反射调用私有方法
                    java.lang.reflect.Method updateMethod = userService.getClass()
                            .getDeclaredMethod("updateLastLoginTime", String.class);
                    updateMethod.setAccessible(true);
                    updateMethod.invoke(userService, username);
                } catch (Exception e) {
                    logger.debug("更新登录时间失败: {}", e.getMessage());
                }
                
                // 如果选择记住用户名，设置Cookie
                if (remember) {
                    Cookie cookie = new Cookie("username", username);
                    cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    logger.info("设置记住用户名Cookie: {}", username);
                }
                
                // 重定向到欢迎页面
                return "redirect:/welcome";
            } else {
                // 登录失败
                logger.warn("登录失败: 用户名或密码错误 - {}", username);
                model.addAttribute("error", "用户名或密码错误");
                return "index";
            }
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            model.addAttribute("error", "系统错误，请稍后再试");
            return "index";
        }
    }

    /**
     * 处理AJAX登录请求
     * @param username 用户名
     * @param password 密码
     * @param remember 是否记住用户名
     * @param session HTTP会话
     * @param response HTTP响应
     * @return JSON响应
     */
    @PostMapping("/api/ajax-login")
    @ResponseBody
    public ResponseEntity<?> ajaxLogin(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam(required = false, defaultValue = "false") boolean remember,
                                   HttpSession session,
                                   HttpServletResponse response) {
        
        logger.info("AJAX用户登录尝试: {}", username);

        // 验证输入参数
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            logger.warn("登录失败: 用户名或密码为空");
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户名和密码不能为空"));
        }

        try {
            // 先检查用户是否存在
            User existingUser = userService.findByUsername(username);
            if (existingUser == null) {
                logger.warn("登录失败: 用户不存在 - {}", username);
                return ResponseEntity.status(401).body(new ApiResponse(false, "用户名或密码错误"));
            }
            
            // 验证密码
            boolean passwordMatch = false;
            try {
                logger.debug("尝试验证密码 - 输入密码: {}, 数据库密码: {}", password, existingUser.getPassword());
                passwordMatch = org.example.util.PasswordUtil.verify(password, existingUser.getPassword());
                logger.info("BCrypt密码验证结果: {}", passwordMatch);
            } catch (Exception e) {
                logger.error("密码验证异常: {}", e.getMessage(), e);
                // 如果BCrypt验证失败，尝试明文比较
                passwordMatch = password.equals(existingUser.getPassword());
                logger.info("明文密码比较结果: {}", passwordMatch);
            }
            
            if (passwordMatch) {
                // 登录成功
                logger.info("用户登录成功: {}", username);
                
                // 将用户信息存储到会话中
                session.setAttribute("user", existingUser);
                
                // 更新最后登录时间
                try {
                    // 使用反射调用私有方法
                    java.lang.reflect.Method updateMethod = userService.getClass()
                            .getDeclaredMethod("updateLastLoginTime", String.class);
                    updateMethod.setAccessible(true);
                    updateMethod.invoke(userService, username);
                } catch (Exception e) {
                    logger.debug("更新登录时间失败: {}", e.getMessage());
                }
                
                // 如果选择记住用户名，设置Cookie
                if (remember) {
                    Cookie cookie = new Cookie("username", username);
                    cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    logger.info("设置记住用户名Cookie: {}", username);
                }
                
                // 返回成功响应
                return ResponseEntity.ok(new ApiResponse(true, "登录成功"));
            } else {
                // 登录失败
                logger.warn("登录失败: 用户名或密码错误 - {}", username);
                return ResponseEntity.status(401).body(new ApiResponse(false, "用户名或密码错误"));
            }
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "系统错误，请稍后再试"));
        }
    }

    /**
     * 处理登出请求
     * @param session HTTP会话
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 重定向到登录页面
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                logger.info("用户登出: {}", user.getUsername());
            }
            
            // 使会话失效
            session.invalidate();
            
            // 清除记住用户名的Cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("username".equals(cookie.getName())) {
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("登出过程中发生异常", e);
        }
        
        return "redirect:/";
    }
    
    /**
     * 处理登录API请求
     * @param loginRequest 登录请求
     * @param session HTTP会话
     * @return 登录结果
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(@RequestBody LoginRequest loginRequest, HttpSession session) {
        logger.info("前端登录API请求: {}", loginRequest.getUsername());
        
        try {
            // 验证输入参数
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                logger.warn("登录失败: 用户名或密码为空");
                return ResponseEntity.badRequest().body(new ApiResponse(false, "用户名和密码不能为空"));
            }
            
            // 调用业务逻辑层进行登录验证
            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (user != null) {
                // 登录成功
                logger.info("用户登录成功: {}", loginRequest.getUsername());
                
                // 将用户信息存储到会话中
                session.setAttribute("user", user);
                
                // 返回成功响应
                return ResponseEntity.ok().body(new ApiResponse(true, "登录成功"));
            } else {
                // 登录失败
                logger.warn("登录失败: 用户名或密码错误 - {}", loginRequest.getUsername());
                return ResponseEntity.ok().body(new ApiResponse(false, "用户名或密码错误"));
            }
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "系统错误，请稍后再试"));
        }
    }
    
    /**
     * 获取当前用户信息API
     * @param session HTTP会话
     * @return 用户信息
     */
    @GetMapping("/api/user")
    @ResponseBody
    public ResponseEntity<?> getCurrentUserApi(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body(new ApiResponse(false, "用户未登录"));
        }
    }
    
    /**
     * 处理登出API请求
     * @param session HTTP会话
     * @return 登出结果
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<?> logoutApi(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                logger.info("用户登出: {}", user.getUsername());
            }
            
            // 使会话失效
            session.invalidate();
            
            return ResponseEntity.ok().body(new ApiResponse(true, "登出成功"));
        } catch (Exception e) {
            logger.error("登出过程中发生异常", e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "系统错误，请稍后再试"));
        }
    }

    /**
     * 显示欢迎页面
     * @param session HTTP会话
     * @param model 模型对象
     * @return 欢迎页面或重定向到登录页面
     */
    @GetMapping("/welcome")
    public String welcome(HttpSession session, Model model) {
        // 检查用户是否已登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 未登录，重定向到登录页面
            return "redirect:/";
        }
        
        // 将用户信息添加到模型中
        model.addAttribute("user", user);
        return "welcome";
    }
    
    /**
     * 显示注册页面
     * @return 注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    /**
     * 显示登录成功页面
     * @return 登录成功页面
     */
    @GetMapping("/login-success")
    public String loginSuccessPage() {
        return "login-success";
    }
    
    /**
     * 处理用户注册请求
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param realName 真实姓名
     * @param model 模型对象
     * @return 注册结果页面或重定向到登录页面
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String realName,
                         @RequestParam(required = false) String fromWelcome,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {
        
        logger.info("用户注册尝试: {}", username);
        
        // 验证输入参数
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            logger.warn("注册失败: 用户名或密码为空");
            model.addAttribute("error", "用户名和密码不能为空");
            return fromWelcome != null ? "welcome" : "register";
        }
        
        if (username.length() < 3 || username.length() > 50) {
            logger.warn("注册失败: 用户名长度不符合要求 - {}", username);
            model.addAttribute("error", "用户名长度必须在3-50个字符之间");
            return fromWelcome != null ? "welcome" : "register";
        }
        
        if (password.length() < 6) {
            logger.warn("注册失败: 密码长度不足 - {}", username);
            model.addAttribute("error", "密码长度不能少于6位");
            return fromWelcome != null ? "welcome" : "register";
        }
        
        try {
            // 创建新用户对象
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(password); // 密码将在服务层加密
            newUser.setEmail(email != null ? email.trim() : null);
            newUser.setRealName(realName != null ? realName.trim() : null);
            
            // 调用服务层进行注册
            boolean success = userService.register(newUser);
            
            if (success) {
                logger.info("用户注册成功: {}", username);
                
                // 如果是从欢迎页面提交的注册，返回欢迎页面并显示成功消息
                if (fromWelcome != null) {
                    // 获取当前登录用户信息
                    User currentUser = (User) session.getAttribute("user");
                    model.addAttribute("user", currentUser);
                    model.addAttribute("registerSuccess", "用户 " + username + " 注册成功！");
                    return "welcome";
                } else {
                    // 从注册页面提交的，跳转到登录成功页面
                    redirectAttributes.addFlashAttribute("message", "注册成功，请使用您的账户登录");
                    return "redirect:/login-success";
                }
            } else {
                logger.warn("注册失败: 用户名已存在 - {}", username);
                String errorMsg = "用户名已存在，请选择其他用户名";
                if (fromWelcome != null) {
                    // 从欢迎页面提交的，返回欢迎页面并显示错误
                    User currentUser = (User) session.getAttribute("user");
                    model.addAttribute("user", currentUser);
                    model.addAttribute("registerError", errorMsg);
                    return "welcome";
                } else {
                    // 从注册页面提交的，返回注册页面
                    model.addAttribute("error", errorMsg);
                    return "register";
                }
            }
        } catch (Exception e) {
            logger.error("用户注册过程中发生异常: {}", username, e);
            String errorMsg = "注册失败，系统错误，请稍后再试";
            if (fromWelcome != null) {
                // 从欢迎页面提交的，返回欢迎页面并显示错误
                User currentUser = (User) session.getAttribute("user");
                model.addAttribute("user", currentUser);
                model.addAttribute("registerError", errorMsg);
                return "welcome";
            } else {
                // 从注册页面提交的，返回注册页面
                model.addAttribute("error", errorMsg);
                return "register";
            }
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
     * API响应DTO
     */
    public static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}