package com.example.super_movie.mapper;

import com.example.super_movie.entity.Test;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */

@Mapper
public interface TestMapper extends BaseMapper<Test> {
    String getMagic(Integer id);

}
