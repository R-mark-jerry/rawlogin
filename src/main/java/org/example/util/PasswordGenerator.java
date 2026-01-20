package org.example.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Scanner;

/**
 * 密码生成工具类
 * 用于生成BCrypt加密的密码，支持任意密码输入
 */
public class PasswordGenerator {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密指定密码
     * @param password 原始密码
     * @return BCrypt加密后的密码
     */
    public static String encrypt(String password) {
        return encoder.encode(password);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===== 密码加密工具 =====");
        System.out.print("请输入要加密的密码: ");
        String password = scanner.nextLine().trim();
        
        if (password.isEmpty()) {
            System.out.println("密码不能为空！");
            return;
        }
        
        // 生成BCrypt加密密码
        String encodedPassword = encrypt(password);
        
        System.out.println("\n===== 加密结果 =====");
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt加密后的密码: " + encodedPassword);
        
        // 验证密码
        System.out.println("\n===== 验证测试 =====");
        System.out.print("请再次输入密码进行验证: ");
        String verifyPassword = scanner.nextLine().trim();
        
        boolean matches = verify(verifyPassword, encodedPassword);
        System.out.println("密码验证结果: " + (matches ? "✓ 验证成功" : "✗ 验证失败"));
        
        // 提供SQL插入语句示例
        System.out.println("\n===== SQL插入语句示例 =====");
        System.out.println("INSERT INTO users (username, password, email, real_name, create_time, status) ");
        System.out.println("VALUES ('your_username', '" + encodedPassword + "', 'email@example.com', '真实姓名', NOW(), 1);");
        
        scanner.close();
    }
}