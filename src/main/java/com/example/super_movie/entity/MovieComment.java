package com.example.super_movie.entity;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Getter
public class MovieComment{

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer userId;

    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String title;

    private Integer movieId;


}
