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
 * @since 2020-05-06
 */
@Data
public class Message{

    private static final long serialVersionUID = 1L;

    private long id;

    private Integer sendId;

    private Integer receiveId;

    private String title;

    private String content;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    public Message(){}

    public Message(int sendId,int receiveId,String title,String content){
        this.sendId=sendId;
        this.receiveId=receiveId;
        this.title=title;
        this.content=content;
    }


}
