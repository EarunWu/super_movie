package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.vo.MovieInfo;

import java.time.LocalDate;

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
    int saveMovie(String name, LocalDate time, String country, int length, String info);

}
