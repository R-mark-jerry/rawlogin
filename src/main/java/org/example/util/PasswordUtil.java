package org.example.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * 使用Spring Security的BCrypt加密
 */
@Component
public class PasswordUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 使用BCrypt加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        return passwordEncoder.encode(password);
    }
    
    /**
     * 验证密码
     * @param inputPassword 输入的密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String inputPassword, String encryptedPassword) {
        return passwordEncoder.matches(inputPassword, encryptedPassword);
    }
}