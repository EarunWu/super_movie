package com.example.super_movie.service;

import com.example.super_movie.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.vo.MessageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-05-06
 */
public interface IMessageService extends IService<Message> {
    List<MessageInfo> getMessageList(int userId,int page,int pageNum);
    boolean sendMessage(int sendId,int receiveId,String title,String content);
    boolean deleteMessage(int id,int userId);
}
