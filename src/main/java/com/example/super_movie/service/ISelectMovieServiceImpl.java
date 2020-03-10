package com.example.super_movie.service;

import java.util.List;
import java.util.Map;

public interface ISelectMovieServiceImpl {
    List<Map<String,Object>> findMovieByKind(Integer kind);
}
