package com.example.super_movie.controller;


import com.example.super_movie.service.IMovieCommentService;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.vo.MovieCommentInfo;
import com.example.super_movie.vo.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-13
 */
@Controller
public class MovieController{
    @Autowired
    IMovieService iMovieService;

    @Autowired
    IMovieCommentService movieCommentService;

    //电影信息页面
    @RequestMapping("/movieInfo")
    public String toMovieInfo(Model model,Integer movieId){
        MovieInfo movieInfo = iMovieService.getMovieInfo(movieId);
        List<ZSetOperations.TypedTuple<Object>> maxLikeList=movieCommentService.getLikeRankIdByMovieId(movieId,0,2);
        List<MovieCommentInfo> movieCommentInfoList=movieCommentService.getCommentList(maxLikeList);
        model.addAttribute("commentList",movieCommentInfoList);
        model.addAttribute("maxLikeList",maxLikeList);
        model.addAttribute("movieInfo",movieInfo);
        return "movie";
    }
    @ResponseBody
    @RequestMapping("/test1")
    public double toTest(){
        LocalDate time=LocalDate.of(1999,11,11);
        for (int i=0;i<50;i++){
            iMovieService.saveMovie("1",time,"1",1,"1");
            System.out.println(i);
        }
        return iMovieService.getAvgScoreById(4396);}
}
