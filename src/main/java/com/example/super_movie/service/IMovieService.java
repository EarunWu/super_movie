package com.example.super_movie.service;

import com.example.super_movie.entity.Movie;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.vo.MovieInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
public interface IMovieService extends IService<Movie> {
    MovieInfo getMovieInfo(Integer movieId);
    double getAvgScoreById(Integer movieId);

}
