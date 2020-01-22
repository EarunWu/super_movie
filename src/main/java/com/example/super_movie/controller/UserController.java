package com.example.super_movie.controller;


import com.example.super_movie.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
@Controller
public class UserController{
    @Autowired
    IUserService userService;


    //注册
    @RequestMapping("/create")
    public String login(Model model, String username, String password, String email){
        System.out.println(username+" "+password+" "+email);
        System.out.println("1111111111111");
        String a="注册成功，请登录邮箱激活账号";
        String b="账号已注册或格式不正确";
        if(userService.doRegister(username,password,email)){
            System.out.println("if里面");
            model.addAttribute("information", a);
        }else{
            System.out.println("else里面");
            model.addAttribute("information", b);
        }
        return "registerResult";

    }

    //激活
    @RequestMapping("/register")
    public String register(Model model,String code){
        String a="激活成功";
        String b="激活失败，请检查相关信息";
        if(userService.activeUser(code)){
            model.addAttribute("information", a);
        }else{
            model.addAttribute("information", b);
        }
        return "registerResult";
    }

}
