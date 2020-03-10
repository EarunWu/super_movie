package com.example.super_movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.super_movie.entity.Person;
import com.example.super_movie.vo.PersonMovie;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
@Mapper
public interface PersonMapper extends BaseMapper<Person> {
    List<PersonMovie> findMovieByPerson(int personId);
    List<Integer> findJobByPersonId(int personId);
}
