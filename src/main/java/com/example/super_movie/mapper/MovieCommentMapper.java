package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.MovieComment;
import org.apache.ibatis.annotations.Mapper;

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
    int postMovieComment(Integer userId, String content, String title, Integer movieId);
}
