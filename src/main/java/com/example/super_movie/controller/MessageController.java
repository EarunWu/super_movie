package com.example.super_movie.controller;


import com.example.super_movie.entity.Message;
import com.example.super_movie.service.IMessageService;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-05-06
 */
@Controller
public class MessageController{
    @Autowired
    IUserService userService;

    @Autowired
    IMessageService messageService;

    @Autowired
    RedisUtil redisUtil;


    //查看收件箱需要登录
    @RequestMapping("message")
    public String toMessageList(Model model,Integer page){
        int userId=1;
        model.addAttribute("writer",userService.getUserInfoById(userId,userId));
        Integer num=(Integer) redisUtil.hget("number","message"+userId);
        int pageNum=num==null?0:num%7>0?(num/7)+1:num/7;
        model.addAttribute("page",pageNum);
        if (page==null||page<1){
            model.addAttribute("messageList",messageService.getMessageList(userId,1,pageNum));
            model.addAttribute("pageNum",1);
            return "message";
        }
        model.addAttribute("pageNum",page);
        model.addAttribute("messageList",messageService.getMessageList(userId,page,pageNum));
        return "message::messageListSpace";
    }

}