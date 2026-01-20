package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Spring Security配置类
 * 配置应用程序的安全策略
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置HTTP安全策略
     * @param http HTTP安全配置对象
     * @return 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF，方便API测试
            .csrf(csrf -> csrf.disable())
            // 配置请求授权规则
            .authorizeHttpRequests(authz -> authz
                // 允许所有请求，由我们的控制器处理认证
                .anyRequest().permitAll()
            )
            // 禁用Spring Security的默认表单登录，使用我们自定义的登录逻辑
            .formLogin(form -> form.disable())
            // 禁用Spring Security的默认登出，使用我们自定义的登出逻辑
            .logout(logout -> logout.disable());
            
        return http.build();
    }

    /**
     * 密码编码器Bean
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * HTTP会话事件发布器
     * @return 会话事件发布器
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}