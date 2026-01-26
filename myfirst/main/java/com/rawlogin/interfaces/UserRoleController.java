package com.rawlogin.interfaces;

import com.rawlogin.application.UserApplicationService;
import com.rawlogin.common.Result;
import com.rawlogin.config.annotation.PreAuthorize;
import com.rawlogin.domain.service.UserRoleDomainService;
import com.rawlogin.application.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户角色管理控制器
 * 提供用户与角色关联管理的API接口
 */
@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {
    
    @Autowired
    private UserRoleDomainService userRoleDomainService;
    
    @Autowired
    private UserApplicationService userApplicationService;
    
    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize(value = "sys:user:view", authenticated = true)
    public Result<List<RoleDTO>> getUserRoles(@PathVariable Integer userId) {
        try {
            // 检查用户是否存在
            if (userApplicationService.getUserById(userId) == null) {
                return Result.error("用户不存在");
            }
            
            List<RoleDTO> roles = userRoleDomainService.getUserRoles(userId);
            return Result.success(roles);
        } catch (Exception e) {
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param requestData 请求数据，包含角色ID列表
     * @return 操作结果
     */
    @PostMapping("/user/{userId}/assign")
    @PreAuthorize(value = "sys:user:edit", authenticated = true)
    public Result<String> assignRolesToUser(@PathVariable Integer userId, @RequestBody Map<String, Object> requestData) {
        try {
            // 检查用户是否存在
            if (userApplicationService.getUserById(userId) == null) {
                return Result.error("用户不存在");
            }
            
            @SuppressWarnings("unchecked")
            List<Integer> roleIds = (List<Integer>) requestData.get("roleIds");
            
            if (roleIds == null || roleIds.isEmpty()) {
                return Result.error("角色ID列表不能为空");
            }
            
            boolean success = userRoleDomainService.assignRolesToUser(userId, roleIds);
            if (success) {
                return Result.success("角色分配成功");
            } else {
                return Result.error("角色分配失败");
            }
        } catch (Exception e) {
            return Result.error("角色分配失败: " + e.getMessage());
        }
    }
    
    /**
     * 移除用户的所有角色
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/user/{userId}")
    @PreAuthorize(value = "sys:user:edit", authenticated = true)
    public Result<String> removeAllRolesFromUser(@PathVariable Integer userId) {
        try {
            // 检查用户是否存在
            if (userApplicationService.getUserById(userId) == null) {
                return Result.error("用户不存在");
            }
            
            boolean success = userRoleDomainService.removeAllRolesFromUser(userId);
            if (success) {
                return Result.success("移除所有角色成功");
            } else {
                return Result.error("移除角色失败");
            }
        } catch (Exception e) {
            return Result.error("移除角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 移除用户的指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/user/{userId}/role/{roleId}")
    @PreAuthorize(value = "sys:user:edit", authenticated = true)
    public Result<String> removeRoleFromUser(@PathVariable Integer userId, @PathVariable Integer roleId) {
        try {
            // 检查用户是否存在
            if (userApplicationService.getUserById(userId) == null) {
                return Result.error("用户不存在");
            }
            
            boolean success = userRoleDomainService.removeRoleFromUser(userId, roleId);
            if (success) {
                return Result.success("移除角色成功");
            } else {
                return Result.error("移除角色失败");
            }
        } catch (Exception e) {
            return Result.error("移除角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleCode 角色代码
     * @return 检查结果
     */
    @GetMapping("/user/{userId}/check/{roleCode}")
    @PreAuthorize(value = "sys:user:view", authenticated = true)
    public Result<Boolean> checkUserHasRole(@PathVariable Integer userId, @PathVariable String roleCode) {
        try {
            // 检查用户是否存在
            if (userApplicationService.getUserById(userId) == null) {
                return Result.error("用户不存在");
            }
            
            boolean hasRole = userRoleDomainService.userHasRole(userId, roleCode);
            return Result.success(hasRole);
        } catch (Exception e) {
            return Result.error("检查用户角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取角色的所有用户ID
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @GetMapping("/role/{roleId}/users")
    @PreAuthorize(value = "sys:role:view", authenticated = true)
    public Result<List<Integer>> getUserIdsByRoleId(@PathVariable Integer roleId) {
        try {
            List<Integer> userIds = userRoleDomainService.getUserIdsByRoleId(roleId);
            return Result.success(userIds);
        } catch (Exception e) {
            return Result.error("获取角色用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有角色代码
     * @param userId 用户ID
     * @return 角色代码列表
     */
    @GetMapping("/user/{userId}/codes")
    @PreAuthorize(value = "sys:user:view", authenticated = true)
    public Result<List<String>> getUserRoleCodes(@PathVariable Integer userId) {
        try {
            // 检查用户是否存在
            if (userApplicationService.getUserById(userId) == null) {
                return Result.error("用户不存在");
            }
            
            List<String> roleCodes = userRoleDomainService.getUserRoleCodes(userId);
            return Result.success(roleCodes);
        } catch (Exception e) {
            return Result.error("获取用户角色代码失败: " + e.getMessage());
        }
    }
}