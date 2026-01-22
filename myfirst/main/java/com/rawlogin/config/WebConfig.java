package com.rawlogin.config;

import com.rawlogin.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器等Web相关设置
 * 支持DDD架构的接口层路径
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**") // 拦截所有/api/路径的请求
                .addPathPatterns("/interfaces/**") // 拦截DDD架构接口层路径
                .excludePathPatterns("/api/auth/login") // 排除登录接口
                .excludePathPatterns("/api/auth/register") // 排除注册接口
                .excludePathPatterns("/interfaces/auth/login") // 排除DDD架构登录接口
                .excludePathPatterns("/interfaces/auth/register"); // 排除DDD架构注册接口
    }
}