package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.ReplyOfComment;
import com.example.super_movie.vo.ReplyOfCommentInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-03-01
 */
public interface IReplyOfCommentService extends IService<ReplyOfComment> {
    List<ReplyOfCommentInfo> getReplyOfCommentByIdAndPage(int id, int page, Integer order);
    int getPageNum(int id);
    int saveReply(int userId, int movieCommentId, int replyId, String content);
}
