package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.mapper.MovieMapper;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;

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
                movieInfo=new MovieInfo(movie.getId(),movie.getName(),movie.getTime(),movie.getCountry(),movie.getLength(),movie.getInfo(),getBaseMapper().findDirectorById(movieId),getBaseMapper().findScreenwriterById(movieId),getBaseMapper().findActorById(movieId),getBaseMapper().findKindByMovieId(movieId),getBaseMapper().findLanguageByMovieId(movieId),getAvgScoreById(movieId));
                redisUtil.set("movieInfo"+movieId,movieInfo,3600);
                System.out.println("mysql中取得movieInfo并存入redis");
            }
        return movieInfo;
    }
    //取平均分
    public double getAvgScoreById(Integer movieId){
        //将score先存进redis的做法
//        if (redisUtil.get("score"+movieId)!=null){
//            return (double)redisUtil.get("score"+movieId);
//        }else {
//            double sum=redisUtil.zCount("scoreSet"+movieId,1,1)+redisUtil.zCount("scoreSet"+movieId,2,2)*2+redisUtil.zCount("scoreSet"+movieId,3,3)*3+redisUtil.zCount("scoreSet"+movieId,4,4)*4+redisUtil.zCount("scoreSet"+movieId,5,5)*5;
//            double avg=sum/redisUtil.zCard("scoreSet"+movieId);
//            avg=(double) Math.round(avg * 20) / 10;
//            redisUtil.set("score"+movieId,avg,3600);
//            System.out.println("计算得score并存入redis");
//            return avg;
//        }
        //不存的做法，直接返回score
            double sum=redisUtil.zCount("scoreSet"+movieId,1,1)+redisUtil.zCount("scoreSet"+movieId,2,2)*2+redisUtil.zCount("scoreSet"+movieId,3,3)*3+redisUtil.zCount("scoreSet"+movieId,4,4)*4+redisUtil.zCount("scoreSet"+movieId,5,5)*5;
            double avg=sum/redisUtil.zCard("scoreSet"+movieId);
            avg=(double) Math.round(avg * 20) / 10;
            return avg;

    }
    public int saveMovie(String name, LocalDate time, String country, int length, String info){
        return getBaseMapper().addMovie(name, time, country, length, info);
    }

}
