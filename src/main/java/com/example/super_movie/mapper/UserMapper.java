package com.example.super_movie.mapper;

import com.example.super_movie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    int save(String username,String email,String password,Integer state,String code);

    int activeUser(String code);

    User getStateByeMail(String email);

}
