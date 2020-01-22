package com.example.super_movie.service.impl;

import com.example.super_movie.entity.Person;
import com.example.super_movie.mapper.PersonMapper;
import com.example.super_movie.service.IPersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.PersonMovie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
            return personMovieList;
        }
    }
    public Person getPersonById(Integer personId){
        Person person=(Person) redisUtil.get("person"+personId);
        if (person==null){
            person=getById(personId);
            redisUtil.set("person"+personId,person);
        }
        return person;
    }
    public List<Integer> getJobByPersonId(Integer personId){
        return getBaseMapper().findJobByPersonId(personId);
    }

}
