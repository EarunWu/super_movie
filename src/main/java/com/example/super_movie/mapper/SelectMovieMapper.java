package com.example.super_movie.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface SelectMovieMapper {
    List<Map<String,Object>> findAllMovie();
}
