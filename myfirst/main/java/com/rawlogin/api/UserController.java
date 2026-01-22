package com.rawlogin.api;

import com.rawlogin.common.Result;
import com.rawlogin.service.UserService;
import com.rawlogin.service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户管理控制器
 * 处理用户增删改查相关请求
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        logger.info("获取所有用户列表");
        return userService.findAllUsers();
    }
    
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        logger.info("根据ID获取用户信息: {}", id);
        return userService.findById(id);
    }
    
    /**
     * 根据条件查询用户
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param status 用户状态（可选）
     * @return 查询结果
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status) {
        logger.info("根据条件查询用户: username={}, email={}, status={}", username, email, status);
        return userService.findUsersByCondition(username, email, status);
    }
    
    /**
     * 创建新用户
     * @param userCreateRequest 用户创建请求
     * @return 创建结果
     */
    @PostMapping
    public Result<User> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        logger.info("创建新用户: {}", userCreateRequest.getUsername());
        
        // 创建用户对象
        User user = new User();
        user.setUsername(userCreateRequest.getUsername());
        user.setPassword(userCreateRequest.getPassword());
        user.setEmail(userCreateRequest.getEmail());
        user.setRealName(userCreateRequest.getRealName());
        user.setStatus(userCreateRequest.getStatus() != null ? userCreateRequest.getStatus() : 1);
        user.setRole(userCreateRequest.getRole() != null ? userCreateRequest.getRole() : "USER");
        
        // 调用服务层
        return userService.register(user);
    }
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userUpdateRequest 用户更新请求
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Integer id,
                                  @RequestBody UserUpdateRequest userUpdateRequest,
                                  HttpServletRequest request) {
        logger.info("更新用户信息: {}", id);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentRole = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 检查权限
        if (!"ADMIN".equals(currentRole)) {
            return Result.error(com.rawlogin.common.ResultCode.FORBIDDEN, "权限不足，只有管理员可以操作用户");
        }
        
        // 防止用户修改自己的角色
        if (currentUserId.equals(id)) {
            return Result.error(com.rawlogin.common.ResultCode.BAD_REQUEST, "不能修改自己的账户");
        }
        
        // 获取现有用户信息
        Result<User> existingUserResult = userService.findById(id);
        if (!existingUserResult.isSuccess()) {
            return existingUserResult;
        }
        User existingUser = existingUserResult.getData();
        
        // 创建用户对象
        User user = new User();
        user.setId(id);
        user.setUsername(userUpdateRequest.getUsername());
        user.setPassword(userUpdateRequest.getPassword());
        user.setEmail(userUpdateRequest.getEmail());
        user.setRealName(userUpdateRequest.getRealName());
        user.setStatus(userUpdateRequest.getStatus());
        // 只有管理员可以修改角色
        if ("ADMIN".equals(currentRole)) {
            user.setRole(userUpdateRequest.getRole());
        } else {
            user.setRole(existingUser.getRole());
        }
        
        // 调用服务层
        return userService.updateUser(user);
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        logger.info("删除用户: {}", id);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentRole = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 检查权限
        if (!"ADMIN".equals(currentRole)) {
            return Result.error(com.rawlogin.common.ResultCode.FORBIDDEN, "权限不足，只有管理员可以操作用户");
        }
        
        // 防止用户删除自己
        if (currentUserId.equals(id)) {
            return Result.error(com.rawlogin.common.ResultCode.BAD_REQUEST, "不能删除自己的账户");
        }
        
        // 调用服务层
        return userService.deleteUser(id);
    }
    
    /**
     * 批量删除用户
     * @param userIds 用户ID列表
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Integer> userIds, HttpServletRequest request) {
        logger.info("批量删除用户: {}", userIds);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentRole = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 检查权限
        if (!"ADMIN".equals(currentRole)) {
            return Result.error(com.rawlogin.common.ResultCode.FORBIDDEN, "权限不足，只有管理员可以操作用户");
        }
        
        // 防止用户删除自己
        if (userIds.contains(currentUserId)) {
            return Result.error(com.rawlogin.common.ResultCode.BAD_REQUEST, "不能删除自己的账户");
        }
        
        // 调用服务层
        return userService.batchDeleteUsers(userIds);
    }
    
    /**
     * 用户创建请求DTO
     */
    public static class UserCreateRequest {
        private String username;
        private String password;
        private String email;
        private String realName;
        private Integer status;
        private String role;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRealName() {
            return realName;
        }
        
        public void setRealName(String realName) {
            this.realName = realName;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
    
    /**
     * 用户更新请求DTO
     */
    public static class UserUpdateRequest {
        private String username;
        private String password;
        private String email;
        private String realName;
        private Integer status;
        private String role;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRealName() {
            return realName;
        }
        
        public void setRealName(String realName) {
            this.realName = realName;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}