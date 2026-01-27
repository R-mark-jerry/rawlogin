package com.rawlogin.infrastructure.repository;

import com.rawlogin.domain.repository.RoleRepository;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.infrastructure.po.RolePO;
import com.rawlogin.infrastructure.persistence.RoleMapper;
import com.rawlogin.application.converter.RoleConverter;
import com.rawlogin.infrastructure.persistence.PermissionMapper;
import com.rawlogin.infrastructure.po.PermissionPO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色仓储实现类
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public List<RoleDTO> findAll() {
        List<RolePO> rolePOs = roleMapper.findAll();
        System.out.println("DEBUG: RoleMapper.findAll() 返回 " + rolePOs.size() + " 个角色");
        for (RolePO rolePO : rolePOs) {
            System.out.println("DEBUG: 角色ID: " + rolePO.getId() + ", 名称: " + rolePO.getName() + ", 代码: " + rolePO.getCode() + ", 状态: " + rolePO.getStatus());
        }
        List<RoleDTO> roleDTOs = RoleConverter.toDTOList(rolePOs);
        
        // 为每个角色加载权限信息
        for (RoleDTO roleDTO : roleDTOs) {
            roleDTO.setPermissions(loadRolePermissions(roleDTO.getId()));
        }
        
        System.out.println("DEBUG: 最终返回 " + roleDTOs.size() + " 个角色DTO");
        return roleDTOs;
    }
    
    @Override
    public RoleDTO findById(Integer id) {
        RolePO rolePO = roleMapper.selectById(id);
        if (rolePO == null) {
            return null;
        }
        RoleDTO roleDTO = RoleConverter.toDTO(rolePO);
        roleDTO.setPermissions(loadRolePermissions(id));
        return roleDTO;
    }
    
    @Override
    public RoleDTO findByCode(String code) {
        RolePO rolePO = roleMapper.findByCode(code);
        if (rolePO == null) {
            return null;
        }
        RoleDTO roleDTO = RoleConverter.toDTO(rolePO);
        roleDTO.setPermissions(loadRolePermissions(rolePO.getId()));
        return roleDTO;
    }
    
    @Override
    public List<RoleDTO> searchRoles(String name, String code, Integer status, Boolean builtIn) {
        List<RolePO> rolePOs = roleMapper.searchRoles(name, code, status, builtIn);
        List<RoleDTO> roleDTOs = RoleConverter.toDTOList(rolePOs);
        
        // 为每个角色加载权限信息
        for (RoleDTO roleDTO : roleDTOs) {
            roleDTO.setPermissions(loadRolePermissions(roleDTO.getId()));
        }
        
        return roleDTOs;
    }
    
    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        RolePO rolePO = RoleConverter.toPO(roleDTO);
        roleMapper.insert(rolePO);
        // 重新查询以获取生成的ID
        RolePO savedPO = roleMapper.selectById(rolePO.getId());
        RoleDTO savedDTO = RoleConverter.toDTO(savedPO);
        savedDTO.setPermissions(loadRolePermissions(savedDTO.getId()));
        return savedDTO;
    }
    
    @Override
    public RoleDTO update(RoleDTO roleDTO) {
        RolePO rolePO = RoleConverter.toPO(roleDTO);
        roleMapper.updateById(rolePO);
        // 重新查询以获取更新后的数据
        RolePO updatedPO = roleMapper.selectById(rolePO.getId());
        RoleDTO updatedDTO = RoleConverter.toDTO(updatedPO);
        updatedDTO.setPermissions(loadRolePermissions(updatedDTO.getId()));
        return updatedDTO;
    }
    
    @Override
    public boolean deleteById(Integer id) {
        return roleMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean batchDeleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return roleMapper.batchDeleteByIds(ids) > 0;
    }
    
    @Override
    public List<RoleDTO> findRolesByUserId(Integer userId) {
        List<RolePO> rolePOs = roleMapper.findByUserId(userId);
        List<RoleDTO> roleDTOs = RoleConverter.toDTOList(rolePOs);
        
        // 为每个角色加载权限信息
        for (RoleDTO roleDTO : roleDTOs) {
            roleDTO.setPermissions(loadRolePermissions(roleDTO.getId()));
        }
        
        return roleDTOs;
    }
    
    /**
     * 加载角色的权限信息
     * @param roleId 角色ID
     * @return 权限代码列表
     */
    private List<String> loadRolePermissions(Integer roleId) {
        if (roleId == null) {
            return new java.util.ArrayList<>();
        }
        
        // 通过角色ID获取权限列表
        List<PermissionPO> permissionPOs =
            permissionMapper.findByRoleId(roleId);
        
        // 如果没有找到权限，返回空列表而不是null
        if (permissionPOs == null || permissionPOs.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 提取权限代码
        return permissionPOs.stream()
                .map(PermissionPO::getCode)
                .collect(Collectors.toList());
    }
}