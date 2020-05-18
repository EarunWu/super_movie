package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.ReplyOfComment;
import com.example.super_movie.vo.ReplyOfCommentInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-03-01
 */
@Mapper
public interface ReplyOfCommentMapper extends BaseMapper<ReplyOfComment> {
    List<ReplyOfCommentInfo> findReplyListByCommentId(int id, int star, int end);
    List<ReplyOfCommentInfo> findReplyListByCommentIdDesc(int id, int start, int end);
    Integer saveReply(int userId, int movieCommentId, int replyId, String content);
    List<ReplyOfCommentInfo> getNewReplyByUserId(int id, int start, int size);
}
