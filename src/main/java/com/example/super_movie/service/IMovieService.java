package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.vo.MovieInfo;
import com.example.super_movie.vo.SelectMovieList;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    List<SelectMovieList> getMovieListByKind(int state, Integer page,Integer order);
    void updateKindNum();
    int getSearchNumByName(String name);
    List<SelectMovieList> searchMovieByName(String name, int page);

}
