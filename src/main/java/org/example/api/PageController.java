package org.example.api;

import org.example.service.UserService;
import org.example.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 页面控制器
 * 简化版的页面控制器，使用common、service、api分层架构
 */
@Controller
@RequestMapping("")
public class PageController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 显示首页
     * @return 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    /**
     * 显示登录页面
     * @return 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "index";
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
            return "redirect:/myfirst/";
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
     * 处理注册请求
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param realName 真实姓名
     * @param fromWelcome 是否来自欢迎页面
     * @param model 模型对象
     * @return 注册结果页面
     */
    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String realName,
                                @RequestParam(required = false) String fromWelcome,
                                HttpSession session,
                                Model model) {
        
        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRealName(realName);
        
        // 调用服务层
        org.example.common.Result<User> result = userService.register(user);
        
        if (fromWelcome != null && "true".equals(fromWelcome)) {
            // 来自欢迎页面的注册请求，返回欢迎页面
            // 获取当前登录用户信息
            User currentUser = (User) session.getAttribute("user");
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
            }
            
            if (result.isSuccess()) {
                model.addAttribute("registerSuccess", result.getMessage());
            } else {
                model.addAttribute("registerError", result.getMessage());
            }
            return "welcome";
        } else {
            // 来自注册页面的请求
            if (result.isSuccess()) {
                return "redirect:/myfirst/login-success?message=" + result.getMessage();
            } else {
                model.addAttribute("error", result.getMessage());
                return "register";
            }
        }
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
     * 处理GET登出请求
     * @param session HTTP会话
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 重定向到登录页面
     */
    @GetMapping("/logout")
    public String logoutGet(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        return logoutPost(session, request, response);
    }
    
    /**
     * 处理POST登出请求
     * @param session HTTP会话
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 重定向到登录页面
     */
    @PostMapping("/logout")
    public String logoutPost(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
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
        } catch (Exception e) {
            // 记录异常但不中断流程
            System.err.println("登出过程中发生异常: " + e.getMessage());
        }
        
        // Spring Security会自动处理会话失效，这里只需要重定向
        return "redirect:/myfirst/";
    }
}