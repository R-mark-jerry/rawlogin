package com.rawlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot应用程序主类
 */
@SpringBootApplication
@ComponentScan("com.rawlogin")
public class RawloginApplication {

    public static void main(String[] args) {
        SpringApplication.run(RawloginApplication.class, args);
        System.out.println("o(><；)oo启动成功o((>ω< ))o");
    }
}