package com.rawlogin.application.converter;

import com.rawlogin.infrastructure.po.RolePO;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.interfaces.vo.RoleVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色对象转换器
 * 负责在不同层次的对象模型之间进行转换
 */
public class RoleConverter {
    
    /**
     * 将DTO转换为PO
     * @param dto DTO对象
     * @return PO对象
     */
    public static RolePO toPO(RoleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        RolePO po = new RolePO();
        po.setId(dto.getId());
        po.setName(dto.getName());
        po.setCode(dto.getCode());
        po.setDescription(dto.getDescription());
        po.setStatus(dto.getStatus());
        po.setCreateTime(dto.getCreateTime());
        po.setUpdateTime(dto.getUpdateTime());
        
        return po;
    }
    
    /**
     * 将PO转换为DTO
     * @param po PO对象
     * @return DTO对象
     */
    public static RoleDTO toDTO(RolePO po) {
        if (po == null) {
            return null;
        }
        
        RoleDTO dto = new RoleDTO();
        dto.setId(po.getId());
        dto.setName(po.getName());
        dto.setCode(po.getCode());
        dto.setDescription(po.getDescription());
        dto.setStatus(po.getStatus());
        dto.setCreateTime(po.getCreateTime());
        dto.setUpdateTime(po.getUpdateTime());
        
        // 设置内置角色标识
        boolean builtIn = "ADMIN".equals(po.getCode()) || "USER".equals(po.getCode());
        dto.setBuiltIn(builtIn);
        
        return dto;
    }
    
    /**
     * 批量将PO列表转换为DTO列表
     * @param pos PO列表
     * @return DTO列表
     */
    public static List<RoleDTO> toDTOList(List<RolePO> pos) {
        if (pos == null) {
            return null;
        }
        
        return pos.stream()
                .map(RoleConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将DTO转换为VO
     * @param dto DTO对象
     * @return VO对象
     */
    public static RoleVO toVO(RoleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        RoleVO vo = new RoleVO();
        vo.setId(dto.getId());
        vo.setName(dto.getName());
        vo.setCode(dto.getCode());
        vo.setDescription(dto.getDescription());
        vo.setStatus(dto.getStatus());
        vo.setCreateTime(dto.getCreateTime());
        vo.setUpdateTime(dto.getUpdateTime());
        vo.setUserCount(dto.getUserCount());
        vo.setBuiltIn(dto.getBuiltIn());
        
        // 设置权限VO列表
        if (dto.getPermissions() != null) {
            List<RoleVO.PermissionVO> permissionVOs = dto.getPermissions().stream()
                    .map(RoleConverter::toPermissionVO)
                    .collect(Collectors.toList());
            vo.setPermissions(permissionVOs);
        }
        
        // 设置操作权限
        vo.setCanEdit(!dto.getBuiltIn());
        vo.setCanDelete(!dto.getBuiltIn() && !"ADMIN".equals(dto.getCode()) && !"USER".equals(dto.getCode()));
        
        return vo;
    }
    
    /**
     * 将PO直接转换为VO（用于简单查询场景）
     * @param po PO对象
     * @return VO对象
     */
    public static RoleVO toVO(RolePO po) {
        if (po == null) {
            return null;
        }
        
        RoleVO vo = new RoleVO();
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setCode(po.getCode());
        vo.setDescription(po.getDescription());
        vo.setStatus(po.getStatus());
        vo.setCreateTime(po.getCreateTime());
        vo.setUpdateTime(po.getUpdateTime());
        
        // 设置内置角色标识
        boolean builtIn = "ADMIN".equals(po.getCode()) || "USER".equals(po.getCode());
        vo.setBuiltIn(builtIn);
        
        // 设置操作权限
        vo.setCanEdit(!builtIn);
        vo.setCanDelete(!builtIn);
        
        return vo;
    }
    
    /**
     * 将权限代码转换为权限VO
     * @param permissionCode 权限代码
     * @return 权限VO
     */
    public static RoleVO.PermissionVO toPermissionVO(String permissionCode) {
        if (permissionCode == null) {
            return null;
        }
        
        RoleVO.PermissionVO permissionVO = new RoleVO.PermissionVO();
        permissionVO.setCode(permissionCode);
        permissionVO.setName(getPermissionDisplayName(permissionCode));
        permissionVO.setDescription(getPermissionDescription(permissionCode));
        permissionVO.setCategory(getPermissionCategory(permissionCode));
        permissionVO.setDisplayName(getPermissionDisplayName(permissionCode));
        
        return permissionVO;
    }
    
    
    /**
     * 批量将DTO列表转换为VO列表
     * @param dtos DTO列表
     * @return VO列表
     */
    public static List<RoleVO> toVOList(List<RoleDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(RoleConverter::toVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 批量将PO列表转换为VO列表
     * @param pos PO列表
     * @return VO列表
     */
    public static List<RoleVO> toVOListFromPO(List<RolePO> pos) {
        if (pos == null) {
            return null;
        }
        
        return pos.stream()
                .map(RoleConverter::toVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取权限显示名称
     * @param permissionCode 权限代码
     * @return 显示名称
     */
    private static String getPermissionDisplayName(String permissionCode) {
        switch (permissionCode) {
            case "sys:user:list": return "查看用户列表";
            case "sys:user:view": return "查看用户详情";
            case "sys:user:create": return "创建用户";
            case "sys:user:edit": return "编辑用户";
            case "sys:user:delete": return "删除用户";
            case "sys:role:view": return "查看角色";
            case "sys:role:create": return "创建角色";
            case "sys:role:edit": return "编辑角色";
            case "sys:role:delete": return "删除角色";
            case "sys:config:view": return "查看配置";
            case "sys:config:edit": return "编辑配置";
            case "sys:log:view": return "查看日志";
            default: return permissionCode;
        }
    }
    
    /**
     * 获取权限描述
     * @param permissionCode 权限代码
     * @return 权限描述
     */
    private static String getPermissionDescription(String permissionCode) {
        switch (permissionCode) {
            case "sys:user:list": return "查看系统中的用户列表";
            case "sys:user:view": return "查看用户的详细信息";
            case "sys:user:create": return "创建新的用户账户";
            case "sys:user:edit": return "编辑用户的基本信息";
            case "sys:user:delete": return "删除用户账户";
            case "sys:role:view": return "查看系统中的角色列表";
            case "sys:role:create": return "创建新的角色";
            case "sys:role:edit": return "编辑角色的基本信息和权限";
            case "sys:role:delete": return "删除角色";
            case "sys:config:view": return "查看系统配置信息";
            case "sys:config:edit": return "修改系统配置";
            case "sys:log:view": return "查看系统操作日志";
            default: return "自定义权限";
        }
    }
    
    /**
     * 获取权限分类
     * @param permissionCode 权限代码
     * @return 权限分类
     */
    private static String getPermissionCategory(String permissionCode) {
        if (permissionCode.startsWith("sys:user:")) {
            return "用户管理";
        } else if (permissionCode.startsWith("sys:role:")) {
            return "角色管理";
        } else if (permissionCode.startsWith("sys:")) {
            return "系统管理";
        } else {
            return "其他";
        }
    }
}