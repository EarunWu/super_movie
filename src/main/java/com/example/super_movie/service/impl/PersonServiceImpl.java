package com.example.super_movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.entity.Person;
import com.example.super_movie.mapper.PersonMapper;
import com.example.super_movie.service.IPersonService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.PersonMovie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements IPersonService {
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;

    //获取影人参演过的电影
    public List<PersonMovie> getMovieByPerson(int personId){
//        List<PersonMovie> personMovieList = getBaseMapper().findMovieByPerson(personId);
//        redisUtil.lSet("personMovieList"+personId,personMovieList);
        List<PersonMovie> objectList=redisTemplate.opsForList().range("personMovieList"+personId,0,5);
        if (objectList.size()!=0){
            return objectList;
        }else {
            List<PersonMovie> personMovieList=getBaseMapper().findMovieByPerson(personId);
            redisTemplate.opsForList().rightPushAll("personMovieList"+personId,personMovieList);
            redisUtil.expire("personMovieList"+personId,3600);
            return personMovieList;
        }
    }
    public Person getPersonById(Integer personId){
        Person person=(Person) redisUtil.get("person"+personId);
        if (person==null){
            person=getById(personId);
            redisUtil.set("person"+personId,person,3600);
        }
        return person;
    }
    public List<Integer> getJobByPersonId(Integer personId){
        return getBaseMapper().findJobByPersonId(personId);
    }

    public boolean addPerson(String name, Boolean sex, LocalDate born,String info,String enName,String country){
        return getBaseMapper().addPerson(name, sex, born,info,enName,country)>0;
    }

    public List<Person> searchPerson(String name){
        return getBaseMapper().searchPerson(name);
    }
    public List<Person> getPersonListByMovieAndJob(int movieId,int job){
        return getBaseMapper().getPersonByMovieIdAndJob(movieId,job);
    }
    public int addPersonForMovie(int personId,int movieId,int job){
        return getBaseMapper().addPersonForMovie(personId, movieId, job);
    }
    public int deletePersonForMovie(int personId,int movieId,int job){
        return getBaseMapper().deletePersonMovie(personId, movieId, job);
    }
}
