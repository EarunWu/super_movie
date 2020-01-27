package com.example.super_movie.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Data
@Accessors(chain = true)
public class MovieComment{

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer userId;

    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String title;


}
