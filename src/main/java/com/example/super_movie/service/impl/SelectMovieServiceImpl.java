package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.mapper.SelectMovieMapper;
import com.example.super_movie.service.ISelectMovieServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class SelectMovieServiceImpl implements ISelectMovieServiceImpl {
    @Autowired
    SelectMovieMapper selectMovieMapper;
    public List<Map<String,Object>> findMovieByKind(Integer kind){
        List<Map<String,Object>> list=selectMovieMapper.findAllMovie();
        return list;
    }
}
