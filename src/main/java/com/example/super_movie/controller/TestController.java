package com.example.super_movie.controller;


import com.example.super_movie.entity.Person;
import com.example.super_movie.service.ITestService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
@Controller
public class TestController{
    @Autowired
    ITestService iTestService;
    @Resource
    private RedisUtil redisUtil;
    @RequestMapping("/login")
    public String getA(){
        System.out.println(iTestService.getMagic(1));
        System.out.println(iTestService.getById(12).getMagicId());
        return "login";
    }
    @RequestMapping("/index")
    public String toIndex(Model model){
        return "index";

    }
    @RequestMapping("/findMovie")
    public String toFindMovie(Model model){
        return "findMovie";

    }
    @RequestMapping("/msg")
    public String toMsg(Model model){
        return "msg";

    }
    @ResponseBody
    @RequestMapping("/redis")
    public String redisSet(){

        System.out.println("person开始存入redis");
//        byte[] bytes=SerializeUtil.serizlize(person);
//        redisUtil.set("jklove", person);
        System.out.println(redisUtil.get("jklove"));
        //return redisUtil.set(key,userEntity,ExpireTime);
        return "succeed";
    }
    @ResponseBody
    @RequestMapping("/get")
    public Object redisget(String key,double min,double max){
        System.out.println(redisUtil.zCount(key, min, max));
        redisUtil.zSet(key,min,"222");
        System.out.println(redisUtil.zGetScore(key,"3keee"));
        Object object1= redisUtil.get("jklove");
        Person person=(Person)object1;
        System.out.println(person.getName());
        return person;
    }
}
