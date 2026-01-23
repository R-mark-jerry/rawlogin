package com.rawlogin.infrastructure.repository;

import com.rawlogin.domain.model.User;
import com.rawlogin.domain.repository.UserRepository;
import com.rawlogin.infrastructure.persistence.UserMapper;
import com.rawlogin.infrastructure.po.UserPO;
import com.rawlogin.application.converter.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储实现
 * 实现领域仓储接口，桥接领域层和基础设施层
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User save(User user) {
        UserPO po = UserConverter.toPO(user);
        userMapper.insert(po);
        return UserConverter.toDomain(po);
    }
    
    @Override
    public Optional<User> findById(Integer id) {
        UserPO po = userMapper.selectById(id);
        return Optional.ofNullable(UserConverter.toDomain(po));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        UserPO po = userMapper.selectByUsername(username);
        return Optional.ofNullable(UserConverter.toDomain(po));
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }
    
    @Override
    public boolean existsByUsernameAndExcludeId(String username, Integer excludeId) {
        return userMapper.existsByUsernameAndExcludeId(username, excludeId);
    }
    
    @Override
    public User update(User user) {
        UserPO po = UserConverter.toPO(user);
        userMapper.updateById(po);
        return UserConverter.toDomain(po);
    }
    
    @Override
    public boolean deleteById(Integer id) {
        int result = userMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    public int deleteByIds(List<Integer> ids) {
        return userMapper.deleteBatchIds(ids);
    }
    
    @Override
    public List<User> findAll() {
        List<UserPO> pos = userMapper.selectList(null);
        return UserConverter.toDomainList(pos);
    }
    
    @Override
    public List<User> findByCondition(String username, String email, Integer status, String role) {
        List<UserPO> pos = userMapper.selectByCondition(username, email, status, role);
        return UserConverter.toDomainList(pos);
    }
    
    @Override
    public boolean updateLastLoginTime(Integer userId) {
        int result = userMapper.updateLastLoginTime(userId);
        return result > 0;
    }
}