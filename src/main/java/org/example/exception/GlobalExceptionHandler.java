package org.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 统一处理应用程序中的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理所有异常
     * @param ex 异常对象
     * @param request HTTP请求
     * @param model 模型对象
     * @return 错误页面
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request, Model model) {
        logger.error("发生异常: {}", ex.getMessage(), ex);
        
        // 将异常信息添加到模型中
        model.addAttribute("errorMessage", "系统发生错误，请稍后再试");
        model.addAttribute("exception", ex);
        model.addAttribute("url", request.getRequestURL());
        
        // 返回错误页面
        return "error";
    }
    
    /**
     * 处理运行时异常
     * @param ex 运行时异常
     * @param request HTTP请求
     * @param model 模型对象
     * @return 错误页面
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, HttpServletRequest request, Model model) {
        logger.error("发生运行时异常: {}", ex.getMessage(), ex);
        
        model.addAttribute("errorMessage", "系统运行时错误，请稍后再试");
        model.addAttribute("exception", ex);
        model.addAttribute("url", request.getRequestURL());
        
        return "error";
    }
    
    /**
     * 处理空指针异常
     * @param ex 空指针异常
     * @param request HTTP请求
     * @param model 模型对象
     * @return 错误页面
     */
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex, HttpServletRequest request, Model model) {
        logger.error("发生空指针异常: {}", ex.getMessage(), ex);
        
        model.addAttribute("errorMessage", "系统内部错误，请稍后再试");
        model.addAttribute("exception", ex);
        model.addAttribute("url", request.getRequestURL());
        
        return "error";
    }
}