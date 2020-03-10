package com.example.super_movie.controller;

import com.example.super_movie.service.ISelectMovieServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SelectMovieController {
    @Autowired
    ISelectMovieServiceImpl selectMovieService;

    @RequestMapping("/selectMovie")
    public String toSelectMovie(Model model, Integer kind){
        model.addAttribute("movieList",selectMovieService.findMovieByKind(kind));
        return "selectMovie";
    }


}
