package com.rawlogin.interfaces;

import com.rawlogin.application.RoleApplicationService;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.interfaces.vo.RoleVO;
import com.rawlogin.application.converter.RoleConverter;
import com.rawlogin.common.Result;
import com.rawlogin.config.annotation.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理接口控制器
 * 处理角色增删改查功能的接口层
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    
    @Autowired
    private RoleApplicationService roleApplicationService;
    
    /**
     * 获取所有角色列表接口
     * @return 角色列表
     */
    @GetMapping
    @PreAuthorize(value = "sys:role:list", authenticated = true)
    public Result<List<RoleVO>> getAllRoles() {
        logger.info("获取所有角色列表");
        
        return roleApplicationService.getAllRoles();
    }
    
    /**
     * 根据ID获取角色信息接口
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    @PreAuthorize(value = "sys:role:view", authenticated = true)
    public Result<RoleVO> getRoleById(@PathVariable Integer id) {
        logger.info("根据ID获取角色信息: {}", id);
        
        return roleApplicationService.getRoleById(id);
    }
    
    /**
     * 根据条件查询角色接口
     * @param name 角色名称（可选）
     * @param code 角色代码（可选）
     * @param status 角色状态（可选）
     * @param builtIn 是否内置角色（可选）
     * @return 查询结果
     */
    @GetMapping("/search")
    @PreAuthorize(value = "sys:role:list", authenticated = true)
    public Result<List<RoleVO>> searchRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Boolean builtIn) {
        logger.info("根据条件查询角色: name={}, code={}, status={}, builtIn={}", name, code, status, builtIn);
        
        return roleApplicationService.searchRoles(name, code, status, builtIn);
    }
    
    /**
     * 创建新角色接口
     * @param roleCreateRequest 角色创建请求
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize(value = "sys:role:create", authenticated = true)
    public Result<RoleVO> createRole(@RequestBody RoleCreateRequest roleCreateRequest) {
        logger.info("创建新角色: {}", roleCreateRequest.getName());
        
        // 创建角色DTO对象
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName(roleCreateRequest.getName());
        roleDTO.setCode(roleCreateRequest.getCode());
        roleDTO.setDescription(roleCreateRequest.getDescription());
        roleDTO.setStatus(roleCreateRequest.getStatus() != null ? roleCreateRequest.getStatus() : 1);
        roleDTO.setPermissions(roleCreateRequest.getPermissions());
        
        // 调用应用服务层
        return roleApplicationService.createRole(roleDTO);
    }
    
    /**
     * 更新角色信息接口
     * @param id 角色ID
     * @param roleUpdateRequest 角色更新请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize(value = "sys:role:edit", authenticated = true)
    public Result<RoleVO> updateRole(@PathVariable Integer id,
                                   @RequestBody RoleUpdateRequest roleUpdateRequest) {
        logger.info("更新角色信息: {}", id);
        
        // 创建角色DTO对象
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(id);
        roleDTO.setName(roleUpdateRequest.getName());
        roleDTO.setCode(roleUpdateRequest.getCode());
        roleDTO.setDescription(roleUpdateRequest.getDescription());
        roleDTO.setStatus(roleUpdateRequest.getStatus());
        roleDTO.setPermissions(roleUpdateRequest.getPermissions());
        
        // 调用应用服务层
        return roleApplicationService.updateRole(roleDTO);
    }
    
    /**
     * 删除角色接口
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(value = "sys:role:delete", authenticated = true)
    public Result<Void> deleteRole(@PathVariable Integer id) {
        logger.info("删除角色: {}", id);
        
        // 调用应用服务层
        return roleApplicationService.deleteRole(id);
    }
    
    /**
     * 批量删除角色接口
     * @param roleIds 角色ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize(value = "sys:role:delete", authenticated = true)
    public Result<Void> batchDeleteRoles(@RequestBody List<Integer> roleIds) {
        logger.info("批量删除角色: {}", roleIds);
        
        // 调用应用服务层
        return roleApplicationService.batchDeleteRoles(roleIds);
    }
    
    /**
     * 获取所有权限
     */
    @GetMapping("/permissions")
    @PreAuthorize(value = "sys:role:view", authenticated = true)
    public Result<List<RoleVO.PermissionVO>> getPermissionList() {
        try {
            // 这里应该从权限服务获取权限列表，简化示例
            List<String> permissionCodes = java.util.Arrays.asList(
                "sys:user:list", "sys:user:view", "sys:user:create", "sys:user:edit", "sys:user:delete",
                "sys:role:view", "sys:role:create", "sys:role:edit", "sys:role:delete",
                "sys:config:view", "sys:config:edit", "sys:log:view"
            );
            
            List<RoleVO.PermissionVO> permissionVOs = permissionCodes.stream()
                    .map(RoleConverter::toPermissionVO)
                    .collect(Collectors.toList());
            
            return Result.success("获取权限列表成功", permissionVOs);
        } catch (Exception e) {
            return Result.error("获取权限列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 公开接口，不需要权限验证
     */
    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("公开接口访问成功", "这是任何人都可以访问的公开接口");
    }
    
    /**
     * 角色创建请求DTO
     */
    public static class RoleCreateRequest {
        private String name;
        private String code;
        private String description;
        private Integer status;
        private List<String> permissions;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public List<String> getPermissions() {
            return permissions;
        }
        
        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
    
    /**
     * 角色更新请求DTO
     */
    public static class RoleUpdateRequest {
        private String name;
        private String code;
        private String description;
        private Integer status;
        private List<String> permissions;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public List<String> getPermissions() {
            return permissions;
        }
        
        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
}