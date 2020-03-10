package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.MovieComment;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
public interface IMovieCommentService extends IService<MovieComment> {
    int postMovieComment(Integer userId, String content, String title, Integer movieId);
}
