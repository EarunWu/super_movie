package com.example.super_movie.vo;

import com.example.super_movie.entity.MovieComment;
import lombok.Data;

@Data
public class MovieCommentInfo extends MovieComment {
    private int like=-1;
    private String username;
    private String name;

}
