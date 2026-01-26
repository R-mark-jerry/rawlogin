package com.rawlogin.config.aspect;

import com.rawlogin.config.annotation.PreAuthorize;
import com.rawlogin.util.JwtUtil;
import com.rawlogin.infrastructure.persistence.PermissionMapper;
import com.rawlogin.infrastructure.po.PermissionPO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限验证切面
 * 处理@PreAuthorize注解的权限验证
 */
@Aspect
@Component
public class AuthorizationAspect {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
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
            
            // 获取用户ID
            Integer userId = jwtUtil.getUserIdFromToken(token);
            
            // 检查权限
            String requiredPermission = preAuthorize.value();
            if (requiredPermission != null && !requiredPermission.isEmpty()) {
                if (!hasPermission(userId, requiredPermission)) {
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
    private boolean hasPermission(Integer userId, String requiredPermission) {
        // 从数据库获取用户权限
        List<PermissionPO> userPermissions = permissionMapper.findByUserId(userId);
        
        // 检查是否包含所需权限
        return userPermissions.stream()
                .anyMatch(permission -> requiredPermission.equals(permission.getCode()));
    }
}