package com.rawlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.rawlogin.config.DddConfig;

/**
 * Spring Boot应用程序主类
 * 采用DDD架构设计
 */
@SpringBootApplication
@Import(DddConfig.class)
public class RawloginApplication {

    public static void main(String[] args) {
        SpringApplication.run(RawloginApplication.class, args);
        System.out.println("o(><；)oo启动成功o((>ω< ))o");
        System.out.println("DDD架构已启用 - 领域驱动设计分层架构");
    }
}