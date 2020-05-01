package com.example.super_movie.util;

import com.example.super_movie.vo.MovieCommentInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: applacation
 * @description:
 * @author: daiwenlong
 * @create: 2019-01-24 13:26
 **/
@Component
public class RedisService {


    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    public void getKey(List<String> keys, String value){
        List<Object> list = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : keys) {
                    connection.get(key.getBytes());
                }
                return null;
            }
        });

    }

    public void insertKey(List<String> keys, String value){


//        //批量get数据
//        List<Object> list = redisTemplate.executePipelined(new RedisCallback<String>() {
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                for (String key : keys) {
//                    connection.get(key.getBytes());
//                }
//                return null;
//            }
//        });

        //批量向set集合插入数据
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i=0;i<keys.size();i++) {
                    connection.sAdd(value.getBytes(),keys.get(i).getBytes());
                }
                return null;
            }
        });
    }
    public void setKeys(List<String> keys, List<MovieCommentInfo> value) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        String jsonString = om.writeValueAsString(value);
        List<Object> list = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : keys) {
                    connection.set(key.getBytes(),value.toString().getBytes());
                }
                return null;
            }
        });

    }
}