package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.MovieComment;
import com.example.super_movie.mapper.MovieCommentMapper;
import com.example.super_movie.service.IMovieCommentService;
import com.example.super_movie.util.RedisService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieCommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Service
public class MovieCommentServiceImpl extends ServiceImpl<MovieCommentMapper, MovieComment> implements IMovieCommentService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedisService redisService;
    public int postMovieComment(Integer userId,String content,String title,Integer movieId,int score){
        MovieComment movieComment=new MovieComment(userId,content,title,movieId,score);
        getBaseMapper().postMovieComment(movieComment);
        return movieComment.getId();
    }

    public boolean getLikeStateById(int userId,Integer commentId, LocalDate localDate){
        String stringDate1=localDate.plusDays(1).toString();
        String stringDate=localDate.toString();
        if (!redisUtil.hasKey(userId+stringDate)){
            //redis中没有记录，批量插入影评当天的点赞记录到redis set
            List<String> a=getBaseMapper().getLikesByDateAndUserId(userId,stringDate,stringDate1);
            redisService.insertKey(a,userId+stringDate);
            redisUtil.expire(userId+stringDate,60*60*24*7);
        }
        //返回查询结果
        return redisUtil.sHasKey(userId+stringDate,commentId);

    }
    public int getLikeNum(int commentId,int movieId){
        Double d= redisUtil.zGetScore("likeNum"+movieId,commentId);
        return d==null?0:d.intValue();
    }
    public boolean like(int userId,Integer commentId,Integer movieId, LocalDate localDate){
        String stringDate1=localDate.plusDays(1).toString();
        String stringDate=localDate.toString();
        if (redisUtil.sSet(userId+stringDate,commentId)==1){
            //点赞成功操作，点赞数加1
            redisUtil.zSetInc("likeNum"+movieId,commentId,1);
            //存入新的点赞记录hash，以备导入数据库
            redisUtil.hset("newLikes",userId+"_"+commentId,1);
            redisUtil.expire("comment"+commentId,60*60*24);
            return true;
        }else {
            //已点过赞，转为取消点赞
            redisUtil.zSetInc("likeNum"+movieId,commentId,-1);
            redisUtil.setRemove(userId+stringDate,commentId);
            //存入新的点赞记录hash，以备导入数据库
            redisUtil.hset("newLikes",userId+"_"+commentId,0);
            return false;
        }
    }
    public MovieCommentInfo getMovieCommentInfoById(int id){
        MovieCommentInfo movieCommentInfo=(MovieCommentInfo) redisUtil.get("comment"+id);
        if (movieCommentInfo==null){
            movieCommentInfo=getBaseMapper().getMovieCommentInfoById(id);
            redisUtil.set("comment"+id,movieCommentInfo,60*60*24);
        }
        if (movieCommentInfo==null){
            System.out.println("数据库不存在此数据");
            return null;
        }
        movieCommentInfo.setLike(getLikeNum(movieCommentInfo.getId(),movieCommentInfo.getMovieId()));
        movieCommentInfo.setState(getLikeStateById(movieCommentInfo.getUserId(),id,movieCommentInfo.getCreateTime().toLocalDate()));
        return movieCommentInfo;
    }
    //获取指定电影的最高点赞的影评
    public List<MovieCommentInfo> getCommentList(List<ZSetOperations.TypedTuple<Object>> scoreList){
        List<String> keys = new ArrayList<>();
        for(int i=0;i<scoreList.size();i++){
            keys.add("comment"+scoreList.get(i).getValue());
        }
        Object ob=redisUtil.get(keys);
        List<MovieCommentInfo> movieCommentInfos=(List<MovieCommentInfo>)ob;
        for (int i=0;i<movieCommentInfos.size();i++){
            if (movieCommentInfos.get(i)==null){
                movieCommentInfos.set(i,getMovieCommentInfoById((int)scoreList.get(i).getValue()));
            }
        }
        return movieCommentInfos;
    }

    //由电影id获取此电影的点赞排行的id
    public List<ZSetOperations.TypedTuple<Object>> getLikeRankIdByMovieId(int movieId,int start,int end){
        Set<ZSetOperations.TypedTuple<Object>> set=redisUtil.zGetRangeWithScore("likeNum"+movieId,start,end);
        return new ArrayList<>(set);
    }
    //获取指定电影的影评按时间排序，按页码取
    public List<MovieCommentInfo> getCommentTimeOrderList(int page,int movieId){
        Integer num=(Integer) redisUtil.hget("number","movieComment"+movieId);
        if (num==null||num==0)
            return null;
        List<MovieCommentInfo> list=redisTemplate.opsForList().range("commentList"+movieId+"_"+page,0,-1);
        if (list==null||list.size()==0){
            list=getBaseMapper().getCommentTimeOrderList(movieId,(page-1)*7,7);
            redisTemplate.opsForList().rightPushAll("commentList"+movieId+"_"+page,list);
            redisUtil.expire("commentList"+movieId+"_"+page,60*60);
        }
        return list;
    }
}
