package com.rawlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                // 允许所有请求，由JWT拦截器处理认证
                .anyRequest().permitAll()
            )
            // 禁用Spring Security的默认表单登录，使用我们自定义的登录逻辑
            .formLogin(form -> form.disable())
            // 禁用HTTP Basic认证
            .httpBasic(httpBasic -> httpBasic.disable())
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
           
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
     * CORS配置源Bean
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许所有源
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 不允许发送Cookie，避免跨域问题
        configuration.setAllowCredentials(false);
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);
        
        // 应用配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}