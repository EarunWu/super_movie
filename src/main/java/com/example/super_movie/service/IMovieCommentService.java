package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.MovieComment;
import com.example.super_movie.vo.MovieCommentInfo;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
public interface IMovieCommentService extends IService<MovieComment> {
    int postMovieComment(Integer userId, String content, String title, Integer movieId,int score);
    boolean getLikeStateById(int userId,Integer commentId, LocalDate localDate);
    boolean like(int userId,Integer commentId, Integer movieId, LocalDate localDate);
    MovieCommentInfo getMovieCommentInfoById(int id);
    List<MovieCommentInfo> getCommentList(List<ZSetOperations.TypedTuple<Object>> scoreList);
    List<ZSetOperations.TypedTuple<Object>> getLikeRankIdByMovieId(int movieId,int start,int end);
    List<MovieCommentInfo> getCommentTimeOrderList(int movieId,int page,int pageNum);
    List<MovieCommentInfo> getCommentListByUserId(int userId,int page,int pageNum);
    List<MovieCommentInfo> getPublicHomeList(Integer page);
    List<MovieCommentInfo> getPrivateHomeList(int userId,int page);
}
