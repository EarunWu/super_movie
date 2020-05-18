package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.MovieComment;
import com.example.super_movie.vo.MovieCommentInfo;
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
 * @since 2020-01-26
 */
@Mapper
public interface MovieCommentMapper extends BaseMapper<MovieComment> {
    int postMovieComment(MovieComment movieComment);
    List<String> getLikesByDateAndUserId(@Param("userId") int userId, @Param("date") String date, @Param("date1") String date1);
    MovieCommentInfo getMovieCommentInfoById(int id);
    List<MovieCommentInfo> getCommentTimeOrderList(int movieId, int start, int number);
    List<MovieCommentInfo> getCommentListByUserId(int userId, int start, int number);
    List<MovieCommentInfo> getPrivateHomeList(Map<String, Object> map);
}
