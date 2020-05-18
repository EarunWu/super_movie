package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.Movie;
import com.example.super_movie.mapper.MovieMapper;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.util.CNHToENG;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieInfo;
import com.example.super_movie.vo.SelectMovieList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${avgNum}")
    private int avgNum;

    //取电影信息
    public MovieInfo getMovieInfo(Integer movieId){
        MovieInfo movieInfo=(MovieInfo)redisUtil.get("movieInfo"+movieId);
        if (movieInfo==null){
                Movie movie=getById(movieId);
                Map<String,Object> map=getBaseMapper().getAvgScoreAndNumByMovieId(movieId);
                BigDecimal avg=(BigDecimal)map.get("avg");
                avg=avg.setScale(1,BigDecimal.ROUND_DOWN);
                Long sum=(Long)map.get("sum");
                movieInfo=new MovieInfo(movie,getBaseMapper().findDirectorById(movieId),getBaseMapper().findScreenwriterById(movieId),getBaseMapper().findActorById(movieId),getBaseMapper().findKindByMovieId(movieId),getBaseMapper().findLanguageByMovieId(movieId),avg.doubleValue(),sum.intValue());
                redisUtil.set("movieInfo"+movieId,movieInfo,3600);
                System.out.println("mysql中取得movieInfo并存入redis");
            }
        return movieInfo;
    }
    public int saveMovie(String name, LocalDate time, String country, int length, String info){
        return getBaseMapper().addMovie(name, time, country, length, info);
    }

    //按时间顺序获取此种类电影list
    public List<SelectMovieList> getMovieListByKindOrderByTime(int state, int page){
        List<SelectMovieList> list=redisTemplate.opsForList().range("kindTime"+state,(page-1)*30,(page-1)*30+30);
        if (list==null||list.size()==0){
            list= getBaseMapper().findMovieListByKindOrderByTime(CNHToENG.getCHNById(state));
            redisUtil.lSetList("kindTime"+state,list);
        }
        return list;
    }
    //按热度顺序获取此种类电影list
    public List<SelectMovieList> getMovieListByKindOrderByHot(int state, int page){
        List<SelectMovieList> list=redisTemplate.opsForList().range("kindHot"+state,(page-1)*30,(page-1)*30+30);
        if (list==null||list.size()==0){
            list= getBaseMapper().findMovieListByKindOrderByHot(CNHToENG.getCHNById(state));
            redisUtil.lSetList("kindHot"+state,list);
        }
        return list;
    }
    //整合以上两个查找
    public List<SelectMovieList> getMovieListByKind(int state, Integer page, Integer order){

        Integer num=(Integer) redisUtil.hget("number","kind"+state);
        if (num==null||num==0)
            return new ArrayList<>();
        num=num%30==0?num/30:(num/30)+1;
        if (page==null||page<1)
            page=1;
        if (page>num)
            page=num;
        if (order==null||order==0)
            return getMovieListByKindOrderByHot(state,page);
        return getMovieListByKindOrderByTime(state,page);
    }

    public void updateKind(){
        for (int i=1;i<7;i++){
            redisUtil.del("kindTime"+i);
            redisUtil.hset("number","kind"+i,getBaseMapper().getKindNumByKind(CNHToENG.getCHNById(i)));
        }
    }
    public int getSearchNumByName(String name){
        return getBaseMapper().getSearchCountNum("%"+name+"%");
    }
    public List<SelectMovieList> searchMovieByName(String name, int page){
        return getBaseMapper().searchMovieByName("%"+name+"%", (page-1)*10);
    }
    public int addNewMovie(String name, LocalDate time, String country,int length,String info){
        Movie movie=new Movie(0, name, time,country,length,info);
        if (getBaseMapper().addNewMovie(movie)>0)
            return movie.getId();
        return -1;
    }
    public int addKindsForMovie(int movieId,String[] kinds){
        Map<String,Object> map=new HashMap<>();
        map.put("movieId",movieId);
        map.put("array",kinds);
        return getBaseMapper().addKindsForMovie(map);
    }
    public int addLanguagesForMovie(int movieId,String[] languages){
        Map<String,Object> map=new HashMap<>();
        map.put("movieId",movieId);
        map.put("array",languages);
        return getBaseMapper().addLanguagesForMovie(map);
    }
    public int addPersonForMovie(int personId,int movieId,int job){
        try {
            return getBaseMapper().addPersonForMovie(personId, movieId, job);
        }catch (Exception e){
            return 0;
        }
    }
    public List<MovieInfo> getMovieRank(int i){
        if (i==1){
            List<MovieInfo> list=redisTemplate.opsForList().range("movieAvgRank",0,-1);
            if (list==null||list.size()==0){
                list=getBaseMapper().getMovieAvgRankList(avgNum);
                redisUtil.lSetList("movieAvgRank",list);
                redisUtil.expire("movieAvgRank",60*60);
            }
            return list;
        }else {
            List<MovieInfo> list=redisTemplate.opsForList().range("movieHotRank",0,-1);
            if (list==null||list.size()==0){
                list=getBaseMapper().getMovieHotRankList();
                redisUtil.lSetList("movieHotRank",list);
                redisUtil.expire("movieHotRank",60*60);
            }
            return list;
        }
    }
}
