package com.rawlogin.common;

/**
 * 统一状态码枚举
 * 定义系统中使用的各种状态码
 */
public class ResultCode {
    
    // 成功状态码
    public static final int SUCCESS = 200;
    
    // 客户端错误状态码
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    
    // 服务器错误状态码
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;
    
    // 业务错误状态码
    public static final int BUSINESS_ERROR = 600;
    public static final int USER_NOT_FOUND = 601;
    public static final int USER_ALREADY_EXISTS = 602;
    public static final int PASSWORD_ERROR = 603;
    public static final int ACCOUNT_DISABLED = 604;
    public static final int TOKEN_INVALID = 605;
    public static final int TOKEN_EXPIRED = 606;
    public static final int PARAM_ERROR = 607 ;
}