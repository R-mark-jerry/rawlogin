package com.rawlogin.application.converter;

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
     * 数据库持久化对象转数据传输对象
     */
    public static UserDTO toDTO(UserPO po) {
        if (po == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(po.getId());
        dto.setUsername(po.getUsername());
        dto.setPassword(po.getPassword());
        dto.setEmail(po.getEmail());
        dto.setRole(po.getRole());
        dto.setStatus(po.getStatus());
        dto.setCreateTime(po.getCreateTime());
        dto.setUpdateTime(po.getUpdateTime());
        dto.setLastLoginTime(po.getLastLoginTime());
        
        return dto;
    }
    
    /**
     * 数据传输对象转数据库持久化对象
     */
    public static UserPO toPO(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        UserPO po = new UserPO();
        po.setId(dto.getId());
        po.setUsername(dto.getUsername());
        po.setPassword(dto.getPassword());
        po.setEmail(dto.getEmail());
        po.setRole(dto.getRole());
        po.setStatus(dto.getStatus());
        po.setCreateTime(dto.getCreateTime());
        po.setUpdateTime(dto.getUpdateTime());
        po.setLastLoginTime(dto.getLastLoginTime());
        
        return po;
    }
    
    /**
     * 数据库持久化对象列表转数据传输对象列表
     */
    public static List<UserDTO> toDTOList(List<UserPO> pos) {
        if (pos == null || pos.isEmpty()) {
            return new ArrayList<>();
        }
        
        return pos.stream()
                .map(UserConverter::toDTO)
                .collect(java.util.stream.Collectors.toList());
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
     * 数据库持久化对象转视图对象
     */
    public static UserVO toVOFromPO(UserPO po) {
        if (po == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        vo.setId(po.getId());
        vo.setUsername(po.getUsername());
        vo.setEmail(po.getEmail());
        vo.setRole(po.getRole());
        vo.setStatus(po.getStatus());
        vo.setCreateTime(po.getCreateTime());
        vo.setUpdateTime(po.getUpdateTime());
        vo.setLastLoginTime(po.getLastLoginTime());
        
        return vo;
    }
    
    /**
     * 数据传输对象列表转视图对象列表
     */
    public static List<UserVO> toVOList(List<UserDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        
        return dtos.stream()
                .map(UserConverter::toVO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 数据库持久化对象列表转视图对象列表
     */
    public static List<UserVO> toVOListFromPO(List<UserPO> pos) {
        if (pos == null) {
            return new ArrayList<>();
        }
        
        return pos.stream()
                .map(UserConverter::toVOFromPO)
                .collect(java.util.stream.Collectors.toList());
    }
}