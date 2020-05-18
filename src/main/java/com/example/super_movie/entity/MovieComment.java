package com.example.super_movie.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Data
public class MovieComment{

    private static final long serialVersionUID = 1L;

    private int id;

    private int userId;

    private String content;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    private String title;

    private int movieId;

    private int score;

    public MovieComment(){}

    public MovieComment(Integer userId,String content,String title,Integer movieId,int score){
        this.userId=userId;
        this.content=content;
        this.title=title;
        this.movieId=movieId;
        this.score=score;
    }


}
