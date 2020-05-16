package com.example.super_movie.entity;


import lombok.Data;

import java.sql.Timestamp;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-03-01
 */
@Data
public class ReplyOfComment{

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer userId;

    private Integer movieCommentId;

    private Integer replyId;

    private String content;

    private Timestamp createTime;

    private int state;


}
