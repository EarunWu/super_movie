package com.example.super_movie.service.impl;

import com.example.super_movie.entity.MovieComment;
import com.example.super_movie.mapper.MovieCommentMapper;
import com.example.super_movie.service.IMovieCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Service
public class MovieCommentServiceImpl extends ServiceImpl<MovieCommentMapper, MovieComment> implements IMovieCommentService {
    public void postMovieComment(Integer userId,String content,String title){
        getBaseMapper().postMovieComment(userId,content,title);
    }
}
