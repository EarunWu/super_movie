package com.example.super_movie.service;

import com.example.super_movie.entity.MovieComment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
public interface IMovieCommentService extends IService<MovieComment> {
    void postMovieComment(Integer userId,String content,String title);
}
