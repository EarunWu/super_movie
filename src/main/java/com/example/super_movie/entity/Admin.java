package com.example.super_movie.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
@Data
public class Admin{

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private Integer lv;


}
