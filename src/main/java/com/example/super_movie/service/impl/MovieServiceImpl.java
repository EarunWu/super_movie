package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.mapper.MovieMapper;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements IMovieService {
    @Resource
    private RedisUtil redisUtil;

    //取电影信息
    public MovieInfo getMovieInfo(Integer movieId){
        MovieInfo movieInfo=(MovieInfo)redisUtil.get("movieInfo"+movieId);
        if (movieInfo==null){
                Movie movie=getById(movieId);
                Map<String,Object> map=getBaseMapper().getAvgScoreAndNumByMovieId(movieId);
                BigDecimal avg=(BigDecimal)map.get("avg");
                avg=avg.setScale(1,BigDecimal.ROUND_DOWN);
                Long sum=(Long)map.get("sum");
                movieInfo=new MovieInfo(movie.getId(),movie.getName(),movie.getTime(),movie.getCountry(),movie.getLength(),movie.getInfo(),getBaseMapper().findDirectorById(movieId),getBaseMapper().findScreenwriterById(movieId),getBaseMapper().findActorById(movieId),getBaseMapper().findKindByMovieId(movieId),getBaseMapper().findLanguageByMovieId(movieId),avg.doubleValue(),sum.intValue());
                redisUtil.set("movieInfo"+movieId,movieInfo,3600);
                System.out.println("mysql中取得movieInfo并存入redis");
            }
        return movieInfo;
    }
    public int saveMovie(String name, LocalDate time, String country, int length, String info){
        return getBaseMapper().addMovie(name, time, country, length, info);
    }

}
