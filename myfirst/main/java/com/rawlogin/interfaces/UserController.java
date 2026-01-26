package com.rawlogin.interfaces;

import com.rawlogin.application.UserApplicationService;
import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.interfaces.vo.UserVO;
import com.rawlogin.domain.service.UserDomainService;
import com.rawlogin.common.Result;
import com.rawlogin.config.annotation.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户管理接口控制器
 * 处理用户增删改查功能的接口层
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserApplicationService userApplicationService;
    
    @Autowired
    private UserDomainService userDomainService;
    
    /**
     * 获取所有用户列表接口
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize(value = "sys:user:list", authenticated = true)
    public Result<List<UserVO>> getAllUsers() {
        logger.info("获取所有用户列表");
        
        return userApplicationService.getAllUsers();
    }
    
    /**
     * 根据ID获取用户信息接口
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize(value = "sys:user:view", authenticated = true)
    public Result<UserVO> getUserById(@PathVariable Integer id, HttpServletRequest request) {
        logger.info("根据ID获取用户信息: {}", id);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentRole = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 检查权限：只有管理员可以查看其他用户详情
        if (!"ADMIN".equals(currentRole) && !currentUserId.equals(id)) {
            return Result.error("权限不足，只有管理员可以查看其他用户信息");
        }
        
        return userApplicationService.getUserById(id);
    }
    
    /**
     * 根据条件查询用户接口
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param status 用户状态（可选）
     * @param role 用户角色（可选）
     * @return 查询结果
     */
    @GetMapping("/search")
    @PreAuthorize(value = "sys:user:list", authenticated = true)
    public Result<List<UserVO>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String role) {
        logger.info("根据条件查询用户: username={}, email={}, status={}, role={}", username, email, status, role);
        
        return userApplicationService.searchUsers(username, email, status, role);
    }
    
    /**
     * 创建新用户接口
     * @param userCreateRequest 用户创建请求
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize(value = "sys:user:create", authenticated = true)
    public Result<UserVO> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        logger.info("创建新用户: {}", userCreateRequest.getUsername());
        
        // 创建用户DTO对象
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userCreateRequest.getUsername());
        userDTO.setPassword(userCreateRequest.getPassword());
        userDTO.setEmail(userCreateRequest.getEmail());
        userDTO.setStatus(userCreateRequest.getStatus() != null ? userCreateRequest.getStatus() : 1);
        userDTO.setRole(userCreateRequest.getRole() != null ? userCreateRequest.getRole() : "USER");
        
        // 调用应用服务层
        return userApplicationService.register(userDTO);
    }
    
    /**
     * 更新用户信息接口
     * @param id 用户ID
     * @param userUpdateRequest 用户更新请求
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize(value = "sys:user:edit", authenticated = true)
    public Result<UserVO> updateUser(@PathVariable Integer id,
                                  @RequestBody UserUpdateRequest userUpdateRequest,
                                  HttpServletRequest request) {
        logger.info("更新用户信息: {}", id);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        String currentRole = (String) request.getAttribute("role");
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 检查权限：只有管理员可以修改其他用户信息
        if (!"ADMIN".equals(currentRole) && !currentUserId.equals(id)) {
            return Result.error("权限不足，只有管理员可以修改其他用户信息");
        }
        
        // 防止普通用户修改自己的角色
        if (!"ADMIN".equals(currentRole) && userUpdateRequest.getRole() != null &&
            !userUpdateRequest.getRole().equals("USER")) {
            return Result.error("普通用户不能修改自己的角色");
        }
        
        // 创建用户DTO对象
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setUsername(userUpdateRequest.getUsername());
        userDTO.setPassword(userUpdateRequest.getPassword());
        userDTO.setEmail(userUpdateRequest.getEmail());
        userDTO.setStatus(userUpdateRequest.getStatus());
        
        // 只有管理员可以修改角色
        if ("ADMIN".equals(currentRole)) {
            userDTO.setRole(userUpdateRequest.getRole());
        }
        
        // 调用应用服务层
        return userApplicationService.updateUser(userDTO);
    }
    
    /**
     * 删除用户接口
     * @param id 用户ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(value = "sys:user:delete", authenticated = true)
    public Result<Void> deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        logger.info("删除用户: {}", id);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 防止用户删除自己
        if (currentUserId.equals(id)) {
            return Result.error("不能删除自己的账户");
        }
        
        // 调用应用服务层
        return userApplicationService.deleteUser(id);
    }
    
    /**
     * 批量删除用户接口
     * @param userIds 用户ID列表
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize(value = "sys:user:delete", authenticated = true)
    public Result<Void> batchDeleteUsers(@RequestBody List<Integer> userIds, HttpServletRequest request) {
        logger.info("批量删除用户: {}", userIds);
        
        // 从请求属性中获取当前用户信息（由JWT拦截器设置）
        Integer currentUserId = (Integer) request.getAttribute("userId");
        
        // 防止用户删除自己
        if (userIds.contains(currentUserId)) {
            return Result.error("不能删除自己的账户");
        }
        
        // 调用应用服务层
        return userApplicationService.batchDeleteUsers(userIds);
    }
    
    /**
     * 用户创建请求DTO
     */
    public static class UserCreateRequest {
        private String username;
        private String password;
        private String email;
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