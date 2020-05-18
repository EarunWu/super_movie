package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.entity.Person;
import com.example.super_movie.vo.MovieInfo;
import com.example.super_movie.vo.SelectMovieList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<SelectMovieList> findMovieListByKindOrderByTime(String kind);
    List<SelectMovieList> findMovieListByKindOrderByHot(String kind);
    Integer getKindNumByKind(String kind);
    List<SelectMovieList> searchMovieByName(@Param("name") String name, @Param("start") int start);
    int getSearchCountNum(String name);
    int addNewMovie(Movie movie);
    int addKindsForMovie(Map<String, Object> map);
    int addPersonForMovie(int personId, int movieId, int job);
    List<MovieInfo> getMovieAvgRankList(int num);
    List<MovieInfo> getMovieHotRankList();
    int addLanguagesForMovie(Map<String, Object> map);
    int banMovie(int id);
    int updateMovie(int id,String name, LocalDate time, String country,int length,String info);
    int deleteKinds(int movieId);
    int deleteLanguages(int movieId);
}
