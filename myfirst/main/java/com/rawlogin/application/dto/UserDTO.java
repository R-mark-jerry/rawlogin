package com.rawlogin.application.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户数据传输对象
 * 用于应用层和接口层之间的数据传输
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastLoginTime;
    
    // 带参数构造函数
    public UserDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }
}