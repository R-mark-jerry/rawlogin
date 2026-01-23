package com.rawlogin.interfaces;

import com.rawlogin.common.Result;
import com.rawlogin.config.annotation.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 * 演示基于角色的权限控制
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    
    /**
     * 获取角色列表
     * 需要 sys:role:view 权限
     */
    @GetMapping("/list")
    @PreAuthorize(value = "sys:role:view", authenticated = true)
    public Result<String> getRoleList() {
        // 模拟获取角色列表
        return Result.success("获取角色列表成功", "[{\"roles\":[{\"id\":1,\"name\":\"管理员\",\"code\":\"ADMIN\"},{\"id\":2,\"name\":\"普通用户\",\"code\":\"USER\"}]}");
    }
    
    /**
     * 创建角色
     * 需要 sys:role:create 权限
     */
    @PostMapping("/create")
    @PreAuthorize(value = "sys:role:create", authenticated = true)
    public Result<String> createRole(@RequestBody String roleData) {
        // 模拟创建角色
        return Result.success("创建角色成功", "角色ID: " + System.currentTimeMillis());
    }
    
    /**
     * 更新角色
     * 需要 sys:role:edit 权限
     */
    @PutMapping("/update/{id}")
    @PreAuthorize(value = "sys:role:edit", authenticated = true)
    public Result<String> updateRole(@PathVariable Integer id, @RequestBody String roleData) {
        // 模拟更新角色
        return Result.success("更新角色成功", "角色ID: " + id);
    }
    
    /**
     * 删除角色
     * 需要 sys:role:delete 权限
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize(value = "sys:role:delete", authenticated = true)
    public Result<String> deleteRole(@PathVariable Integer id) {
        // 模拟删除角色
        return Result.success("删除角色成功", "已删除角色ID: " + id);
    }
    
    /**
     * 获取角色的数据权限范围
     * 需要 sys:role:edit 权限
     */
    @GetMapping("/dataScope/{id}")
    @PreAuthorize(value = "sys:role:edit", authenticated = true)
    public Result<String> getDataScope(@PathVariable Integer id) {
        // 模拟获取数据权限范围
        return Result.success("获取数据权限范围成功", "角色ID: " + id + "的数据权限范围");
    }
    
    /**
     * 更新角色的数据权限范围
     * 需要 sys:role:edit 权限
     */
    @PostMapping("/dataScope")
    @PreAuthorize(value = "sys:role:edit", authenticated = true)
    public Result<String> updateDataScope(@RequestBody String dataScopeData) {
        // 模拟更新数据权限范围
        return Result.success("更新数据权限范围成功", "数据权限范围已更新");
    }
    
    /**
     * 公开接口，不需要权限验证
     */
    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("公开接口访问成功", "这是任何人都可以访问的公开接口");
    }
}