package com.example.super_movie.vo;

import com.example.super_movie.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private int id;
    private String username;
    private String introduction;
    private int followNum=-1;
    private boolean followState;
}
