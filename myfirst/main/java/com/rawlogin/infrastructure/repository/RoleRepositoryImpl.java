package com.rawlogin.infrastructure.repository;

import com.rawlogin.domain.repository.RoleRepository;
import com.rawlogin.application.dto.RoleDTO;
import com.rawlogin.infrastructure.po.RolePO;
import com.rawlogin.infrastructure.persistence.RoleMapper;
import com.rawlogin.application.converter.RoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色仓储实现类
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Override
    public List<RoleDTO> findAll() {
        List<RolePO> rolePOs = roleMapper.findAllEnabled();
        return RoleConverter.toDTOList(rolePOs);
    }
    
    @Override
    public RoleDTO findById(Integer id) {
        RolePO rolePO = roleMapper.selectById(id);
        return rolePO != null ? RoleConverter.toDTO(rolePO) : null;
    }
    
    @Override
    public RoleDTO findByCode(String code) {
        RolePO rolePO = roleMapper.findByCode(code);
        return rolePO != null ? RoleConverter.toDTO(rolePO) : null;
    }
    
    @Override
    public List<RoleDTO> searchRoles(String name, String code, Integer status, Boolean builtIn) {
        List<RolePO> rolePOs = roleMapper.searchRoles(name, code, status, builtIn);
        return RoleConverter.toDTOList(rolePOs);
    }
    
    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        RolePO rolePO = RoleConverter.toPO(roleDTO);
        roleMapper.insert(rolePO);
        // 重新查询以获取生成的ID
        RolePO savedPO = roleMapper.selectById(rolePO.getId());
        return RoleConverter.toDTO(savedPO);
    }
    
    @Override
    public RoleDTO update(RoleDTO roleDTO) {
        RolePO rolePO = RoleConverter.toPO(roleDTO);
        roleMapper.updateById(rolePO);
        return roleDTO;
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
        return RoleConverter.toDTOList(rolePOs);
    }
}