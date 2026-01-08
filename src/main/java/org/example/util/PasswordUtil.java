package org.example.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类
 */
public class PasswordUtil {
    
    /**
     * 使用MD5加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // 如果加密失败，返回原始密码
        }
    }
    
    /**
     * 验证密码
     * @param inputPassword 输入的密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String inputPassword, String encryptedPassword) {
        return encrypt(inputPassword).equals(encryptedPassword);
    }
}