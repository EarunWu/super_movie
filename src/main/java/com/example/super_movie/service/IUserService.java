package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.User;
import com.example.super_movie.vo.UserInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
public interface IUserService extends IService<User> {
    int doRegister(String username, String password, String email);

    boolean activeUser(String code);

    UserInfo getUserInfoById(int userId, int id);

    int follow(int userId, int followId);

    List<UserInfo> getBlackList(int userId);

    int addBlackList(int userId, int id);

    int removeBlackList(int userId, int id);

    int updateUserInfo(String username, String introduction, int userId);

    int updatePassword(String newPassword, int userId, String password);

    boolean getFollowState(int userId, int followId);

    User login(String email, String password);

    int banUser(Integer userId);

}
