package com.example.super_movie.controller;


import com.example.super_movie.service.IAdminService;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
@Controller
@RequestMapping("/admin")
public class AdminController{
    @Autowired
    IAdminService adminService;
    @Autowired
    IMovieService movieService;
    @Autowired
    IPersonService personService;

    @ResponseBody
    @RequestMapping("/addRecommend")
    public int addRecommend(int id){
        return adminService.pushMovieToRecommend(id)?1:0;
    }

    @ResponseBody
    @RequestMapping("/removeRecommend")
    public int removeRecommend(){
        return adminService.removeAllRecommend()?1:0;
    }

    @ResponseBody
    @RequestMapping("/removeReply")
    public int removeReply(int id){
        return adminService.removeReply(id)?1:0;
    }

    @ResponseBody
    @RequestMapping("/banUser")
    public int banUser(int id,Integer state){
        if (state==null)
            return adminService.banUser(id)?1:0;
        return !adminService.unBanUser(id)?1:0;
    }

    @ResponseBody
    @RequestMapping("/banMovieComment")
    public int banMovieComment(int id,Integer state){
        if (state==null)
            return adminService.banMovieComment(id)?1:0;
        return !adminService.unBanMovieComment(id)?1:0;
    }

    @ResponseBody
    @RequestMapping("/ban")
    public int ban(int id,Integer state){
        if (state==null)
            return adminService.banMovieComment(id)?1:0;
        return adminService.unBanMovieComment(id)?1:0;
    }


    @ResponseBody
    @RequestMapping("/addMovie")
    public int addMovie(String name, String time, String country,int length,String info){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(time, fmt);
        return movieService.addNewMovie(name, date, country, length, info);
    }

    @ResponseBody
    @RequestMapping("/addPerson")
    public int addPerson(String name, Boolean sex, String born,String info,String enName,String country){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(born, fmt);
        return personService.addPerson(name, sex, date, info, enName, country)?1:0;
    }

    @ResponseBody
    @RequestMapping("/addKindForMovie")
    public int addKindForMovie(int kind,int movieId){
        return movieService.addKindForMovie(movieId,kind);
    }

    @ResponseBody
    @RequestMapping("/addPersonForMovie")
    public int addPersonForMovie(int personId,int movieId,int job){
        return movieService.addPersonForMovie(personId, movieId, job);
    }

    @ResponseBody
    @RequestMapping("updateKindNum")
    public String updateKindNum(){
        movieService.updateKindNum();
        return "msg";
    }

}
