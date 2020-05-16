package com.example.super_movie.controller;


import com.example.super_movie.service.IMovieCommentService;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.util.CNHToENG;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieCommentInfo;
import com.example.super_movie.vo.MovieInfo;
import com.example.super_movie.vo.SelectMovieList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
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
    IMovieService movieService;

    @Autowired
    IMovieCommentService movieCommentService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    //电影信息页面
    @RequestMapping("/movieInfo")
    public String toMovieInfo(Model model,Integer movieId,HttpServletRequest request){
        MovieInfo movieInfo = movieService.getMovieInfo(movieId);
        List<ZSetOperations.TypedTuple<Object>> maxLikeList=movieCommentService.getLikeRankIdByMovieId(movieId,0,2);
        List<MovieCommentInfo> movieCommentInfoList=movieCommentService.getCommentList(maxLikeList);
        model.addAttribute("commentList",movieCommentInfoList);
        model.addAttribute("maxLikeList",maxLikeList);
        model.addAttribute("loginId",request.getSession().getAttribute("userId"));
        model.addAttribute("movieInfo",movieInfo);
        model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
        return "movie";
    }

    @RequestMapping("/movieSelect")
    public String toMovieSelect(Model model,Integer state,Integer page,Integer order,HttpServletRequest request){
        //检测state是否合法
        if (state==null||state<1||state>6)
            state=1;
        int pageNum=(int)redisUtil.hget("number","kind"+state);
        pageNum=pageNum%30==0?pageNum/30:(pageNum/30)+1;
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("page",page==null?1:page);
        model.addAttribute("kind", CNHToENG.getCHNById(state));
        model.addAttribute("state", state);
        model.addAttribute("loginId",request.getSession().getAttribute("userId"));
        model.addAttribute("order",order==null?0:1);
        model.addAttribute("movieList",movieService.getMovieListByKind(state,page,order));
        return "selectMovie";
    }

    @RequestMapping("index")
    public String toIndex(HttpServletRequest request, Model model, Integer page, Integer state, Integer reload){
        int pageNum;
        if (state==null){
            pageNum=(int)redisUtil.lGetListSize("publicHome");
            pageNum=pageNum%5==0?pageNum/5:pageNum/5+1;
            if (page==null||page<1||page>pageNum)
                page=1;
            model.addAttribute("movieCommentList",movieCommentService.getPublicHomeList(page));
        }
        else{
            Integer userId=(Integer)request.getSession().getAttribute("userId");
            if (userId==null)
                return "redirect:/login";
            List<MovieCommentInfo> list=movieCommentService.getPrivateHomeList(userId,page);
            model.addAttribute("movieCommentList",list);
            pageNum=(int)redisUtil.lGetListSize("privateHome"+userId);
            pageNum=pageNum%5==0?pageNum/5:pageNum/5+1;
            if (page==null||page<1||page>pageNum)
                page=1;
        }
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("page",page);
        model.addAttribute("state",state==null?0:state);
        if (reload==null){
            model.addAttribute("loginId",request.getSession().getAttribute("userId"));
            model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
            return "index";
        }
        return "index::commentListSpace";
    }
    @RequestMapping("/search")
    public String search(HttpServletRequest request,Model model,String name,Integer page,Integer reload){
        if (name==null)
            return "redirect:/index";
        int pageNum=movieService.getSearchNumByName(name);
        pageNum=pageNum%10==0?pageNum/10:pageNum/10+1;
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("name",name);
        model.addAttribute("loginId",request.getSession().getAttribute("userId"));
        if (pageNum==0){
            model.addAttribute("movieList",new ArrayList<>());
            model.addAttribute("page",0);
            model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
            return "search";
        }

        if (page==null||page>pageNum||page<1)
            page=1;
        model.addAttribute("movieList",movieService.searchMovieByName(name,page));
        model.addAttribute("page",page);
        if (reload==null){
            model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
            return "search";
        }
        return "search::movieListSpace";
    }
    @RequestMapping("/movieRank")
    public String getMovieRank(HttpServletRequest request,Model model, Integer i){
        model.addAttribute("movieList",movieService.getMovieRank(i==null?0:1));
        model.addAttribute("i",i);
        model.addAttribute("loginId",request.getSession().getAttribute("userId"));
        return "movieRank";
    }
}
