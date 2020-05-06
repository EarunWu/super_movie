package com.example.super_movie.mapper;

import com.example.super_movie.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.vo.MessageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-05-06
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    List<MessageInfo> getMessageList(@Param("userId")int userId, @Param("date")String date);
    int saveMessage(Message message);
    boolean deleteMessageByIdAndReceiveId(int id,int userId);
}
