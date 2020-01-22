package com.example.super_movie.service;

import com.example.super_movie.entity.Person;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.vo.PersonMovie;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
public interface IPersonService extends IService<Person> {
    public List<PersonMovie> getMovieByPerson(int personId);
    Person getPersonById(Integer personId);
    List<Integer> getJobByPersonId(Integer personId);

}
