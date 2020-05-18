package com.example.super_movie.controller;


import com.example.super_movie.service.IAdminService;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.service.IPersonService;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieCommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    IUserService userService;

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


    @RequestMapping("/movie")
    public String addMovie(){
        return "admin/addMovie";
    }

    @ResponseBody
    @RequestMapping("/addMovie")
    public int addMovie(String name, String time, String country,int length,String info,String[] lan,String[] kind){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(time, fmt);
        int movieId=movieService.addNewMovie(name, date, country, length, info);
        if (movieId>0)
            if (kind!=null)
                movieService.addKindsForMovie(movieId,kind);
            if (lan!=null)
                movieService.addLanguagesForMovie(movieId,lan);
        return 1;
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
    public int addKindForMovie(){
        return movieService.addKindsForMovie(1, new String[]{"1","2"});
    }

    @ResponseBody
    @RequestMapping("/addPersonForMovie")
    public int addPersonForMovie(int personId,int movieId,int job){
        return movieService.addPersonForMovie(personId, movieId, job);
    }

    @ResponseBody
    @RequestMapping("updateKind")
    public String updateKindNum(){
        movieService.updateKind();
        return "msg";
    }

    @RequestMapping("/reportList")
    public String toReportList(Model model){
        int num=(int)redisUtil.zCard("commentReport");
        if (num!=0)
            model.addAttribute("reportList",adminService.getCommentReportList(1));
        else
            model.addAttribute("reportList",new ArrayList<>());
        model.addAttribute("num",num);
        return "admin/reportList";
    }
    @RequestMapping("/reportList1")
    public String toReportList1(Model model){
        int num=(int)redisUtil.zCard("replyReport");
        if (num!=0)
            model.addAttribute("reportList",adminService.getReplyReportList(1));
        else
            model.addAttribute("reportList",new ArrayList<>());
        model.addAttribute("num",num);
        return "admin/reportList1";
    }
    @ResponseBody
    @RequestMapping("delMovieComment")
    public int delMovieComment(int movieId,int commentId, int score){
        return adminService.banMovieComment(movieId,commentId,score)?1:0;
    }
    @ResponseBody
    @RequestMapping("delReply")
    public int delReply(int replyId, int score){
        return adminService.removeReply(replyId,score)?1:0;
    }

    @ResponseBody
    @RequestMapping("skipReport")
    public int skip(int score,Integer state){
        if (state!=null)
            return (int)redisUtil.zRemoveByScore("replyReport",score,score);
        return (int)redisUtil.zRemoveByScore("commentReport",score,score);
    }

    @ResponseBody
    @RequestMapping("delAndBanMovieComment")
    public int delAndBanMovieComment(int userId,int movieId,int commentId, int score){
        adminService.banMovieComment(movieId,commentId,score);
        return userService.banUser(userId);
    }

    @ResponseBody
    @RequestMapping("delAndBanReply")
    public int delAndBanReply(int userId,int replyId, int score){
        adminService.removeReply(replyId,score);
        return userService.banUser(userId);
    }

    @RequestMapping("/index")
    public String kk(){
        return "admin/index";
    }

    @RequestMapping("/moviePic")
    public String addMoviePic(){
        return "admin/addMoviePic";
    }


}
