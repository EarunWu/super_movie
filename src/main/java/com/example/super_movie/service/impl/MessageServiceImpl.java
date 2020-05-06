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
    public boolean sendMessage(int sendId,int receiveId,String title,String content){
        try {
            Message message=new Message(sendId,receiveId,title,content);
            getBaseMapper().saveMessage(message);
            System.out.println("=="+message.getId());
            redisUtil.del("messageList"+receiveId);
            redisUtil.hincr("number","message"+receiveId,1);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;

    }
    public boolean deleteMessage(int id,int userId){
        return getBaseMapper().deleteMessageByIdAndReceiveId(id,userId);
    }

}
