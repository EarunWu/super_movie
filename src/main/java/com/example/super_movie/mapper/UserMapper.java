package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.User;
import com.example.super_movie.vo.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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
    int save(String username, String email, String password, Integer state, String code);

    int activeUser(String code);

    User getStateByeMail(String email);

    UserInfo getUserInfoById(int id);

    List<String> getFollowByUserId(int userId);

}
