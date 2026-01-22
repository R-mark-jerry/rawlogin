package com.rawlogin.infrastructure.repository;

import com.rawlogin.domain.model.User;
import com.rawlogin.domain.repository.UserRepository;
import com.rawlogin.infrastructure.persistence.UserMapper;
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
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public Optional<User> findById(Integer id) {
        User user = userMapper.selectById(id);
        return Optional.ofNullable(user);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        return Optional.ofNullable(user);
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
        userMapper.updateById(user);
        return user;
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
        return userMapper.selectList(null);
    }
    
    @Override
    public List<User> findByCondition(String username, String email, Integer status, String role) {
        return userMapper.selectByCondition(username, email, status, role);
    }
    
    @Override
    public boolean updateLastLoginTime(Integer userId) {
        int result = userMapper.updateLastLoginTime(userId);
        return result > 0;
    }
}