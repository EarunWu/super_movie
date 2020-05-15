package com.example.super_movie.controller;


import com.example.super_movie.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-05-15
 */
@Controller
public class AdminController{
    @Autowired
    IAdminService adminService;

    @ResponseBody
    @RequestMapping("/admin/addRecommend")
    public int addRecommend(int id){
        return adminService.pushMovieToRecommend(id)?1:0;
    }

}
