package com.example.super_movie.service;

import com.example.super_movie.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
public interface IAdminService extends IService<Admin> {
    boolean pushMovieToRecommend(int id);
    boolean removeAllRecommend();
    boolean banUser(int userId);
    boolean unBanUser(int userId);
    boolean banMovieComment(int id);
    boolean unBanMovieComment(int id);
    boolean removeReply(int id);

}
