package com.rawlogin.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DDD 架构配置类
 * 负责扫描和注册 DDD 分层架构中的所有组件
 */
@Configuration
@ComponentScan(basePackages = {
    "com.rawlogin.domain",
    "com.rawlogin.application", 
    "com.rawlogin.infrastructure",
    "com.rawlogin.interfaces",
    "com.rawlogin.common",
    "com.rawlogin.util"
})
@EnableTransactionManagement
public class DddConfig {
    // DDD 分层架构配置
    // 确保所有层次的组件都被正确扫描和注册
}