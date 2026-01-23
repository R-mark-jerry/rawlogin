package com.rawlogin.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义权限校验注解
 * 用于标记方法需要的权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthorize {
    
    /**
     * 权限表达式
     * @return 权限字符串
     */
    String value() default "";
    
    /**
     * 是否需要认证
     * @return true表示需要认证
     */
    boolean authenticated() default true;
}