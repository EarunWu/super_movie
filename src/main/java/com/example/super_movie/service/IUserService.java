package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
public interface IUserService extends IService<User> {
    boolean doRegister(String username, String password, String email);

    boolean activeUser(String code);

}
