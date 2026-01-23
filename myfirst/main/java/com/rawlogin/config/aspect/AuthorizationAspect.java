package com.rawlogin.config.aspect;

import com.rawlogin.config.annotation.PreAuthorize;
import com.rawlogin.util.JwtUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限验证切面
 * 处理@PreAuthorize注解的权限验证
 */
@Aspect
@Component
public class AuthorizationAspect {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 环绕通知，处理权限验证
     * @param joinPoint 连接点
     * @param preAuthorize 权限注解
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(preAuthorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PreAuthorize preAuthorize) throws Throwable {
        // 如果不需要认证，直接执行方法
        if (!preAuthorize.authenticated()) {
            return joinPoint.proceed();
        }
        
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 从请求头中获取JWT令牌
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未提供有效的认证令牌");
        }
        
        token = token.substring(7); // 移除"Bearer "前缀
        
        try {
            // 验证令牌
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("令牌无效或已过期");
            }
            
            // 获取用户角色
            String userRole = jwtUtil.getRoleFromToken(token);
            
            // 检查权限
            String requiredPermission = preAuthorize.value();
            if (requiredPermission != null && !requiredPermission.isEmpty()) {
                if (!hasPermission(userRole, requiredPermission)) {
                    throw new RuntimeException("权限不足，需要权限：" + requiredPermission);
                }
            }
            
            // 执行目标方法
            return joinPoint.proceed();
            
        } catch (Exception e) {
            throw new RuntimeException("权限验证失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否有指定权限
     * @param userRole 用户角色
     * @param requiredPermission 需要的权限
     * @return 是否有权限
     */
    private boolean hasPermission(String userRole, String requiredPermission) {
        // 简单的权限检查逻辑
        // 可以根据需要扩展为更复杂的权限系统
        
        // 管理员拥有所有权限
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // 检查具体权限
        switch (requiredPermission) {
            case "sys:user:list":
                return true; // 所有用户都可以查看用户列表
            case "sys:user:edit":
                return "ADMIN".equals(userRole); // 只有管理员可以编辑用户
            case "sys:user:delete":
                return "ADMIN".equals(userRole); // 只有管理员可以删除用户
            case "sys:role:edit":
                return "ADMIN".equals(userRole); // 只有管理员可以编辑角色
            default:
                return false;
        }
    }
}