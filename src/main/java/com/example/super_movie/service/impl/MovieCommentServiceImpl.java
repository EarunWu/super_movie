package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.MovieComment;
import com.example.super_movie.mapper.MovieCommentMapper;
import com.example.super_movie.service.IMovieCommentService;
import com.example.super_movie.service.IUserService;
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
    @Autowired
    IUserService userService;
    public int postMovieComment(Integer userId,String content,String title,Integer movieId,int score){
        MovieComment movieComment=new MovieComment(userId,content,title,movieId,score);
        getBaseMapper().postMovieComment(movieComment);
        int id=movieComment.getId();
        redisUtil.hincr("number","movieComment"+movieId,1);
        redisUtil.hincr("number","userComment"+userId,1);
        //标记存在此影评
        redisUtil.setBit("commentState",id,true);
        //标记点赞数
        redisUtil.zSet("likeNum"+movieId,0,id);
        //插入公共动态
        redisUtil.lSetHead("publicHome",id);
        return id;
    }

    public boolean getLikeStateById(int userId,Integer commentId, LocalDate localDate){
        String stringDate1=localDate.plusDays(1).toString();
        String stringDate=localDate.toString();
        if (!redisUtil.hasKey(userId+stringDate)){
            //redis中没有记录，批量插入影评当天的点赞记录到redis set
            List<String> a=getBaseMapper().getLikesByDateAndUserId(userId,stringDate,stringDate1);
            redisService.insertKey(a,userId+stringDate);
            //存个0防止set被清空自动删除key
            redisUtil.sSet(userId+stringDate,0);
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
        if (!redisUtil.getBit("commentState",id)){
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
    public List<MovieCommentInfo> getCommentTimeOrderList(int movieId,int page,int pageNum){
        if (pageNum==0)
            return new ArrayList<>();
        List<MovieCommentInfo> list=redisTemplate.opsForList().range("commentList"+movieId+"_"+page,0,-1);
        if (list==null||list.size()==0){
            list=getBaseMapper().getCommentTimeOrderList(movieId,(page-1)*7,7);
            redisTemplate.opsForList().rightPushAll("commentList"+movieId+"_"+page,list);
            redisUtil.expire("commentList"+movieId+"_"+page,60*60);
        }
        return list;
    }
    //获取用户的影评，时间倒序
    public List<MovieCommentInfo> getCommentListByUserId(int userId,int page,int pageNum){
        //过滤
        if (pageNum==0||!redisUtil.getBit("userState",userId))
            return new ArrayList<>();
        List<MovieCommentInfo> list=redisTemplate.opsForList().range("userCommentList"+userId+"_"+page,0,-1);
        if (list==null||list.size()==0){
            list=getBaseMapper().getCommentListByUserId(userId,(page-1)*7,7);
            redisTemplate.opsForList().rightPushAll("userCommentList"+userId+"_"+page,list);
            redisUtil.expire("userCommentList"+userId+"_"+page,60*60);
        }
        return list;
    }
    //获取主页的公共动态信息；
    public List<MovieCommentInfo> getPublicHomeList(Integer page){
        List list=redisUtil.lGet("publicHome",(page-1)*5,(page-1)*5+4);
        List<String> keys=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            keys.add("comment"+list.get(i));
        }
        Object o=redisUtil.get(keys);
        List<MovieCommentInfo> movieCommentInfoList=(List<MovieCommentInfo>)o;
        for (int i=0;i<list.size();i++){
            if (movieCommentInfoList.get(i)==null)
                movieCommentInfoList.set(i,getMovieCommentInfoById((int)list.get(i)));
        }
        return movieCommentInfoList;
    }

    public List<MovieCommentInfo> getPrivateHomeList(int userId,int page){
        userService.getFollowState(userId,0);
        Set set=redisUtil.sGet("follow"+userId);
        List<MovieCommentInfo> list=redisTemplate.opsForList().range("privateHome"+userId,(page-1)*5,(page-1)*5+4);
        if (list==null||list.size()==0){
            Map<String,Object> map=new HashMap<>();
            LocalDate localDate=LocalDate.now().minusMonths(1);
            map.put("list",new ArrayList<>(set));
            map.put("date",localDate.toString());
            list=getBaseMapper().getPrivateHomeList(map);
            list.add(new MovieCommentInfo());
            redisTemplate.opsForList().rightPushAll("privateHome"+userId,list);
            redisUtil.expire("privateHome"+userId,5*60);
            try {
                return list.subList((page-1)*5,(page-1)*5+5);
            }catch (Exception e){
                System.out.println("越界");
                return list.subList((page-1)*5,-1);
            }
        }
        return list;

    }

//    public int report(int state,int id,int userId){
//        //0为举报影评，1为举报评论
//        if (state==0){
//            //把点赞数设置为举报人的id
//            MovieCommentInfo movieCommentInfo=getMovieCommentInfoById(id);
//            movieCommentInfo.setLike(userId);
//            redisUtil.zSetInc("commentReport",movieCommentInfo,1);
//        }
//        if (state==1){
//            redisUtil.lSet("replyReport",id);
//        }
//    }
}
