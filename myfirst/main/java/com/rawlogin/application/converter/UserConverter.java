package com.rawlogin.application.converter;

import com.rawlogin.domain.model.User;
import com.rawlogin.infrastructure.po.UserPO;
import com.rawlogin.application.dto.UserDTO;
import com.rawlogin.interfaces.vo.UserVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户对象转换器
 * 负责在不同层次的对象模型之间进行转换
 */
public class UserConverter {
    
    /**
     * 领域模型转数据库持久化对象
     */
    public static UserPO toPO(User domainUser) {
        if (domainUser == null) {
            return null;
        }
        
        UserPO po = new UserPO();
        po.setId(domainUser.getId());
        po.setUsername(domainUser.getUsername());
        po.setPassword(domainUser.getPassword());
        po.setEmail(domainUser.getEmail());
        po.setRole(domainUser.getRole());
        po.setStatus(domainUser.getStatus());
        po.setCreateTime(domainUser.getCreateTime());
        po.setUpdateTime(domainUser.getUpdateTime());
        po.setLastLoginTime(domainUser.getLastLoginTime());
        
        return po;
    }
    
    /**
     * 数据库持久化对象转领域模型
     */
    public static User toDomain(UserPO po) {
        if (po == null) {
            return null;
        }
        
        User domainUser = new User();
        domainUser.setId(po.getId());
        domainUser.setUsername(po.getUsername());
        domainUser.setPassword(po.getPassword());
        domainUser.setEmail(po.getEmail());
        domainUser.setRole(po.getRole());
        domainUser.setStatus(po.getStatus());
        domainUser.setCreateTime(po.getCreateTime());
        domainUser.setUpdateTime(po.getUpdateTime());
        domainUser.setLastLoginTime(po.getLastLoginTime());
        
        return domainUser;
    }
    
    /**
     * 数据库持久化对象列表转领域模型列表
     */
    public static List<User> toDomainList(List<UserPO> pos) {
        if (pos == null || pos.isEmpty()) {
            return new ArrayList<>();
        }
        
        return pos.stream()
                .map(UserConverter::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 领域模型转数据传输对象
     */
    public static UserDTO toDTO(User domainUser) {
        if (domainUser == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(domainUser.getId());
        dto.setUsername(domainUser.getUsername());
        dto.setEmail(domainUser.getEmail());
        dto.setRole(domainUser.getRole());
        dto.setStatus(domainUser.getStatus());
        dto.setCreateTime(domainUser.getCreateTime());
        dto.setUpdateTime(domainUser.getUpdateTime());
        dto.setLastLoginTime(domainUser.getLastLoginTime());
        
        return dto;
    }
    
    /**
     * 数据传输对象转领域模型
     */
    public static User toDomain(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User domainUser = new User();
        domainUser.setId(dto.getId());
        domainUser.setUsername(dto.getUsername());
        domainUser.setEmail(dto.getEmail());
        domainUser.setRole(dto.getRole());
        domainUser.setStatus(dto.getStatus());
        domainUser.setCreateTime(dto.getCreateTime());
        domainUser.setUpdateTime(dto.getUpdateTime());
        domainUser.setLastLoginTime(dto.getLastLoginTime());
        
        return domainUser;
    }
    
    /**
     * 数据传输对象转视图对象
     */
    public static UserVO toVO(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        vo.setId(dto.getId());
        vo.setUsername(dto.getUsername());
        vo.setEmail(dto.getEmail());
        vo.setRole(dto.getRole());
        vo.setStatus(dto.getStatus());
        vo.setCreateTime(dto.getCreateTime());
        vo.setUpdateTime(dto.getUpdateTime());
        vo.setLastLoginTime(dto.getLastLoginTime());
        
        return vo;
    }
    
    /**
     * 领域模型转视图对象
     */
    public static UserVO toVO(User domainUser) {
        if (domainUser == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        vo.setId(domainUser.getId());
        vo.setUsername(domainUser.getUsername());
        vo.setEmail(domainUser.getEmail());
        vo.setRole(domainUser.getRole());
        vo.setStatus(domainUser.getStatus());
        vo.setCreateTime(domainUser.getCreateTime());
        vo.setUpdateTime(domainUser.getUpdateTime());
        vo.setLastLoginTime(domainUser.getLastLoginTime());
        
        return vo;
    }
    
    /**
     * 领域模型列表转视图对象列表
     */
    public static List<UserVO> toVOList(List<User> domainUsers) {
        if (domainUsers == null) {
            return new ArrayList<>();
        }
        
        return domainUsers.stream()
                .map(UserConverter::toVO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 数据传输对象列表转视图对象列表
     */
    public static List<UserVO> toVOListFromDTO(List<UserDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        
        return dtos.stream()
                .map(UserConverter::toVO)
                .collect(java.util.stream.Collectors.toList());
    }
}