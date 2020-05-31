package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.Admin;
import com.example.super_movie.entity.ReplyOfComment;
import com.example.super_movie.vo.MovieCommentInfo;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
public interface IAdminService extends IService<Admin> {
    boolean pushMovieToRecommend(int id);
    boolean removeAllRecommend();
    boolean unBanUser(int userId);
    boolean banMovieComment(int movieId, Integer id, int score);
    boolean unBanMovieComment(int id);
    boolean removeReply(int id, int score);
    List<ZSetOperations.TypedTuple<MovieCommentInfo>> getCommentReportList(int page);
    List<ZSetOperations.TypedTuple<ReplyOfComment>> getReplyReportList(int page);
    Admin getAdmin(String username);
}
