package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.entity.Person;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
@Mapper
public interface MovieMapper extends BaseMapper<Movie> {
    List<Person> findActorById(Integer id);
    List<Person> findDirectorById(Integer id);
    List<Person> findScreenwriterById(Integer id);
    List<String> findKindByMovieId(int movieId);
    List<String> findLanguageByMovieId(int movieId);
    int addMovie(String name, LocalDate time, String country, int length, String info);
    Map<String,Object> getAvgScoreAndNumByMovieId(int movieId);
}
