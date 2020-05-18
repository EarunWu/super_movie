package com.example.super_movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.super_movie.entity.Person;
import com.example.super_movie.vo.PersonMovie;

import java.time.LocalDate;
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
    boolean addPerson(String name, Boolean sex, LocalDate born, String info, String enName, String country);
    List<Person> searchPerson(String name);
    List<Person> getPersonListByMovieAndJob(int movieId,int job);
    int addPersonForMovie(int personId,int movieId,int job);
    int deletePersonForMovie(int personId,int movieId,int job);

}
