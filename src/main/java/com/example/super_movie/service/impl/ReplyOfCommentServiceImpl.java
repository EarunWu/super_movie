package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.ReplyOfComment;
import com.example.super_movie.mapper.ReplyOfCommentMapper;
import com.example.super_movie.service.IReplyOfCommentService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.ReplyOfCommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-03-01
 */
@Service
public class ReplyOfCommentServiceImpl extends ServiceImpl<ReplyOfCommentMapper, ReplyOfComment> implements IReplyOfCommentService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ReplyOfCommentServiceImpl replyOfCommentService;


    //根据影评id和页码获取评论
    public List<ReplyOfCommentInfo> getReplyOfCommentByIdAndPage(int id,int page,Integer order){
        Integer num=(Integer) redisUtil.hget("number","commentReply"+id);
        if (num==null||num==0)
            return new ArrayList<>();
        if (order==null||order==0) {
            List<ReplyOfCommentInfo> object=redisTemplate.opsForList().range("reply"+id+"_"+page+"_0",0,-1);
            if (object==null||object.size()==0){
                List<ReplyOfCommentInfo> replyList=getBaseMapper().findReplyListByCommentId(id,(page-1)*5,5);
                redisTemplate.opsForList().rightPushAll("reply"+id+"_"+page+"_0",replyList);
                redisTemplate.expire("reply"+id+"_"+page+"_0",3600, TimeUnit.SECONDS);
                return replyList;
            }else {
                return object;
            }
        }else {

            return getBaseMapper().findReplyListByCommentIdDesc(id,(page-1)*5,5);
        }

    }

    //获取总页数
    public int getPageNum(int id){
        Integer num=(Integer)redisUtil.hget("number","commentReply"+id);
        if (num==null)
            return 0;
        return (num%5)>0?(num/5)+1:(num/5);

    }
    public  int saveReply(int userId,int movieCommentId,int replyId,String content){
        int page=replyOfCommentService.getPageNum(movieCommentId);
        if (redisTemplate.opsForList().range("reply"+movieCommentId+"_"+page+"_0",0,-1).size()<5){
            redisTemplate.delete("reply"+movieCommentId+"_"+page+"_0");
        }
        return getBaseMapper().saveReply(userId, movieCommentId, replyId, content);
    }
}
