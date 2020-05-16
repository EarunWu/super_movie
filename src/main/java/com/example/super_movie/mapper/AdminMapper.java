package com.example.super_movie.mapper;

import com.example.super_movie.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.vo.SelectMovieList;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    SelectMovieList getASelectMovie(int movieId);
    int deleteReply(int id);

}
