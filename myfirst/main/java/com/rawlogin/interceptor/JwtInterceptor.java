package com.rawlogin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rawlogin.common.Result;
import com.rawlogin.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT拦截器
 * 用于验证JWT令牌的有效性
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取请求头中的令牌
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendErrorResponse(response, Result.error(401, "未提供认证令牌"));
            return false;
        }
        
        // 去除"Bearer "前缀
        token = token.substring(7);
        
        try {
            // 验证令牌
            if (!jwtUtil.validateToken(token)) {
                sendErrorResponse(response, Result.error(401, "认证令牌无效或已过期"));
                return false;
            }
            
            // 将用户信息存储到请求属性中，供控制器使用
            String username = jwtUtil.getUsernameFromToken(token);
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            request.setAttribute("username", username);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            
            return true;
        } catch (Exception e) {
            logger.error("JWT令牌验证失败", e);
            sendErrorResponse(response, Result.error(401, "认证令牌无效"));
            return false;
        }
    }
    
    /**
     * 发送错误响应
     * @param response HTTP响应
     * @param result 错误结果
     * @throws IOException IO异常
     */
    private void sendErrorResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}