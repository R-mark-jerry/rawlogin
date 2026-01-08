package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.example.service.impl.UserServiceImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private UserService userService = new UserServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        logger.info("用户登录尝试: " + username);

        try {
            // 验证输入参数
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                logger.warning("登录失败: 用户名或密码为空");
                request.setAttribute("error", "用户名和密码不能为空");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }

            User user = userService.login(username, password);
            if (user != null) {
                // 登录成功
                logger.info("用户登录成功: " + username);
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // 记住用户名
                if ("on".equals(remember)) {
                    Cookie cookie = new Cookie("username", username);
                    cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    logger.info("设置记住用户名Cookie: " + username);
                }
                
                response.sendRedirect("welcome.jsp");
            } else {
                // 登录失败
                logger.warning("登录失败: 用户名或密码错误 - " + username);
                request.setAttribute("error", "用户名或密码错误");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "登录过程中发生异常", e);
            request.setAttribute("error", "系统错误，请稍后再试");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            try {
                HttpSession session = req.getSession(false);
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    if (user != null) {
                        logger.info("用户登出: " + user.getUsername());
                    }
                    session.invalidate();
                }
                
                // 清除记住用户名的Cookie
                Cookie[] cookies = req.getCookies();
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
                
                response.sendRedirect("index.jsp");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "登出过程中发生异常", e);
                response.sendRedirect("index.jsp");
            }
        } else {
            response.sendRedirect("index.jsp");
        }
    }
}
