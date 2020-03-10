package com.example.super_movie.vo;

import com.example.super_movie.entity.ReplyOfComment;
import lombok.Data;

@Data
public class ReplyOfCommentInfo extends ReplyOfComment {
    private String username;

    public ReplyOfCommentInfo(){};
}
