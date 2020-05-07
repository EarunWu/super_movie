package com.example.super_movie.service.impl;

import com.example.super_movie.entity.Message;
import com.example.super_movie.mapper.MessageMapper;
import com.example.super_movie.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-05-06
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;

    public List<MessageInfo> getMessageList(int userId, int page,int pageNum){
        if (pageNum==0||page>pageNum)
            return new ArrayList<>();
        page=(page-1)*7;
        List<MessageInfo> list=redisTemplate.opsForList().range("messageList"+userId,page,page+6);
        String date=LocalDate.now().minusDays(30).toString();
        if (list==null||list.size()==0){
            list=getBaseMapper().getMessageList(userId,date);
            if (list==null||list.size()==0){
                redisUtil.hset("number","message"+userId,0);
                return list;
            }
            redisUtil.hset("number","message"+userId,list.size());
            redisTemplate.opsForList().rightPushAll("messageList"+userId,list);
            redisUtil.expire("messageList"+userId,24*60*60);
        }
        return  redisTemplate.opsForList().range("messageList"+userId,page,page+6);
    }
//有两种方案，一种是存入数据库同时即删除相应redis的key，一种是在存入数据库时同时push到redislist
    public int sendMessage(int sendId,int receiveId,String title,String content){
        //检测用户是否激活
        if (!redisUtil.getBit("userState",receiveId))
            return 0;
        //检测是否在黑名单里
        if (redisUtil.sHasKey("receiveBlackList"+receiveId,sendId))
            return -1;
        Message message=new Message(sendId,receiveId,title,content);
        getBaseMapper().saveMessage(message);
        redisUtil.del("messageList"+receiveId);
        redisUtil.hincr("number","message"+receiveId,1);
        //标记有新信息
        redisUtil.setBit("messageState",receiveId,true);
        return 1;

    }

    public boolean deleteMessage(int id,int userId){
        //防止缓存穿透在删除方法加个bitmap过滤器
        if (redisUtil.setBit("messageDeleteState",id,true))
            return false;
        redisUtil.del("messageList"+userId);
        return getBaseMapper().deleteMessageByIdAndReceiveId(id,userId);
    }

}
