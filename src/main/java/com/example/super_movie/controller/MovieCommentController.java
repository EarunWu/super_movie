package com.example.super_movie.controller;


import com.example.super_movie.service.IMovieCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-26
 */
@Controller
public class MovieCommentController{
    @Autowired
    IMovieCommentService movieCommentService;

    @RequestMapping("postMovieComment")
    @ResponseBody
    public String postMovieComment(Integer userId,String content,String title){
        movieCommentService.postMovieComment(userId, content,title);
        return "1111";
    }

    //获取影评页面
    @RequestMapping("movieComment")
    public String toMovieCommentPage(Model model, Integer id){
        model.addAttribute("movieComment",movieCommentService.getById(id));
        return "movieComment";
    }


}
