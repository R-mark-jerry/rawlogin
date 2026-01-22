package com.rawlogin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 - 简化版本
 * 提供JWT令牌的生成、解析和验证功能
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private Long expirationTime;
    
    // 简单的令牌存储，实际应用中应使用数据库或缓存
    private Map<String, TokenInfo> tokenStore = new HashMap<>();
    
    /**
     * 令牌信息类
     */
    private static class TokenInfo {
        String username;
        Integer userId;
        String role;
        long expirationTime;
        
        TokenInfo(String username, Integer userId, String role, long expirationTime) {
            this.username = username;
            this.userId = userId;
            this.role = role;
            this.expirationTime = expirationTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
    
    /**
     * 生成JWT令牌
     * @param username 用户名
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT令牌
     */
    public String generateToken(String username, Integer userId, String role) {
        // 生成简单的令牌（实际应用中应使用真正的JWT）
        String token = "TOKEN_" + System.currentTimeMillis() + "_" + username;
        
        // 存储令牌信息
        long expiration = System.currentTimeMillis() + (expirationTime != null ? expirationTime : 86400000L);
        tokenStore.put(token, new TokenInfo(username, userId, role, expiration));
        
        return token;
    }
    
    /**
     * 从JWT令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        return tokenInfo != null ? tokenInfo.username : null;
    }
    
    /**
     * 从JWT令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        return tokenInfo != null ? tokenInfo.userId : null;
    }
    
    /**
     * 从JWT令牌中获取用户角色
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        return tokenInfo != null ? tokenInfo.role : null;
    }
    
    /**
     * 从JWT令牌中获取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        return tokenInfo != null ? new Date(tokenInfo.expirationTime) : null;
    }
    
    /**
     * 检查JWT令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        return tokenInfo == null || tokenInfo.isExpired();
    }
    
    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null || tokenInfo.isExpired()) {
            return false;
        }
        return tokenInfo.username.equals(username);
    }
    
    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        return tokenInfo != null && !tokenInfo.isExpired();
    }
    
    /**
     * 使令牌失效（登出时使用）
     * @param token JWT令牌
     */
    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }
}