package com.rawlogin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rawlogin.service.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户Mapper接口
 * 使用MyBatis-Plus进行数据库操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Integer userId);
}