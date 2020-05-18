package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.Admin;
import com.example.super_movie.entity.ReplyOfComment;
import com.example.super_movie.mapper.AdminMapper;
import com.example.super_movie.service.IAdminService;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieCommentInfo;
import com.example.super_movie.vo.SelectMovieList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IUserService userService;

    public boolean pushMovieToRecommend(int id){
        SelectMovieList movie=getBaseMapper().getASelectMovie(id);
        return redisUtil.lSet("recommend",movie);
    }

    public boolean removeAllRecommend(){
        try {
            redisUtil.del("recommend");
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public boolean unBanUser(int userId){
        return redisUtil.setBit("userState",userId,true);
    }

    public boolean banMovieComment(int movieId,Integer id,int score){
        redisUtil.zRemove("likeNum"+movieId,id);
        redisUtil.zRemoveByScore("commentReport",score,score);
        return redisUtil.setBit("commentState",id,false);
    }

    public boolean unBanMovieComment(int id){
        return redisUtil.setBit("commentState",id,true);
    }

    public boolean removeReply(int id,int score){
        redisUtil.zRemoveByScore("replyReport",score,score);
        return getBaseMapper().deleteReply(id)>0;
    }

    public List<ZSetOperations.TypedTuple<MovieCommentInfo>> getCommentReportList(int page){
        Set<ZSetOperations.TypedTuple<Object>> set=redisUtil.zGetRangeWithScoreDesc("commentReport",0,9);
        Object ob=new ArrayList<Object>(set);
        List<ZSetOperations.TypedTuple<MovieCommentInfo>> list=(List<ZSetOperations.TypedTuple<MovieCommentInfo>>)ob;
        return list;
    }

    public List<ZSetOperations.TypedTuple<ReplyOfComment>> getReplyReportList(int page){
        Set<ZSetOperations.TypedTuple<Object>> set=redisUtil.zGetRangeWithScoreDesc("replyReport",0,9);
        Object ob=new ArrayList<Object>(set);
        List<ZSetOperations.TypedTuple<ReplyOfComment>> list=(List<ZSetOperations.TypedTuple<ReplyOfComment>>)ob;
        return list;
    }



}
