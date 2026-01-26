package com.rawlogin.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 角色数据传输对象 (DTO)
 * 用于应用层和接口层之间的数据传输
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    
    /**
     * 角色ID
     */
    private Integer id;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色代码
     */
    private String code;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 角色权限列表
     */
    private List<String> permissions;
    
    /**
     * 角色用户数量
     */
    private Integer userCount;
    
    /**
     * 是否为内置角色
     */
    private Boolean builtIn;
    
    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "启用" : "禁用";
    }
    
    /**
     * 获取状态CSS类
     */
    public String getStatusClass() {
        if (status == null) {
            return "status-unknown";
        }
        return status == 1 ? "status-enabled" : "status-disabled";
    }
    
    /**
     * 获取格式化的创建时间
     */
    public String getCreateTimeFormatted() {
        if (createTime == null || createTime.isEmpty()) {
            return "未知";
        }
        try {
            // 简单的时间格式化，实际项目中可能需要更复杂的处理
            return createTime.substring(0, 19).replace("T", " ");
        } catch (Exception e) {
            return createTime;
        }
    }
    
    /**
     * 获取格式化的更新时间
     */
    public String getUpdateTimeFormatted() {
        if (updateTime == null || updateTime.isEmpty()) {
            return "未知";
        }
        try {
            // 简单的时间格式化，实际项目中可能需要更复杂的处理
            return updateTime.substring(0, 19).replace("T", " ");
        } catch (Exception e) {
            return updateTime;
        }
    }
}