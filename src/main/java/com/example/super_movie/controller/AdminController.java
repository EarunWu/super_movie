package com.example.super_movie.controller;


import com.example.super_movie.entity.Admin;
import com.example.super_movie.entity.Person;
import com.example.super_movie.service.IAdminService;
import com.example.super_movie.service.IMovieService;
import com.example.super_movie.service.IPersonService;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.FileNameUtils;
import com.example.super_movie.util.FileUtils;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.MovieCommentInfo;
import com.example.super_movie.vo.SelectMovieList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${web.upload-path}")
    private String path;


    @RequestMapping("login")
    public String toLogin(){
        return "admin/login";
    }
    @RequestMapping("loginIn")
    public String loginIn(String username,String password){
        Integer num=(Integer) redisUtil.get("admin"+username);
        if (num!=null&&num>3){
            System.out.println("密码错误次数太多");
            return "admin/login";
        }
        Admin admin=adminService.getAdmin(username);
        if (admin!=null&&admin.getPassword().equals(password))
            return "admin/index";
        redisUtil.incr("admin"+username,1);
        return "admin/login";
    }

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

    @RequestMapping("/addMovie")
    public String addMovie(String name, String time, String country,int length,String info,String[] lan,String[] kind){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(time, fmt);
        int movieId=movieService.addNewMovie(name, date, country, length, info);
        if (movieId>0)
            if (kind!=null)
                movieService.addKindsForMovie(movieId,kind);
            if (lan!=null)
                movieService.addLanguagesForMovie(movieId,lan);
        return "redirect:personForMovie?movieId="+movieId;
    }

    @RequestMapping("updateMovie")
    public String update(Model model,int id){
        model.addAttribute("movie",movieService.getBaseMapper().selectById(id));
        model.addAttribute("id",id);
        return "admin/updateMovie";
    }


    @RequestMapping("toUpdateMovie")
    public String updateMovie(int id,String name, String time, String country,int length,String info,String[] lan,String[] kind){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(time, fmt);
        movieService.updateMovie(id,name,date,country,length,info,lan,kind);
        return "redirect:personForMovie?movieId="+id;
    }

    @ResponseBody
    @RequestMapping("banMovie")
    public int banMovie(int movieId){
        return movieService.banMovie(movieId);
    }

    @ResponseBody
    @RequestMapping("/toAddPerson")
    public int addPerson(String name, Boolean sex, String born,String info,String engName,String country){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(born, fmt);
        return personService.addPerson(name, sex, date, info, engName, country);
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

    @RequestMapping("/recommend")
    public String search(Model model,String name,Integer page,Integer reload){
        int pageNum=movieService.getSearchNumByName(name);
        pageNum=pageNum%10==0?pageNum/10:pageNum/10+1;
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("name",name);
        if (pageNum==0||name==null){
            model.addAttribute("movieList",new ArrayList<>());
            model.addAttribute("page",0);
            model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
            return "admin/recommend";
        }

        if (page==null||page>pageNum||page<1)
            page=1;
        model.addAttribute("movieList",movieService.searchMovieByName(name,page));
        model.addAttribute("page",page);
        if (reload==null){
            model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
            return "admin/recommend";
        }
        return "admin/recommend::movieListSpace";
    }

    @RequestMapping("personForMovie")
    public String personForMovie(Model model,Integer movieId,String name){
        model.addAttribute("job1",personService.getPersonListByMovieAndJob(movieId,1));
        model.addAttribute("job2",personService.getPersonListByMovieAndJob(movieId,2));
        model.addAttribute("job3",personService.getPersonListByMovieAndJob(movieId,3));
        model.addAttribute("movie",movieService.getBaseMapper().selectById(movieId));
        model.addAttribute("name",name);
        if (name==null)
            model.addAttribute("personList",personService.searchPerson(""));
        else
            model.addAttribute("personList",personService.searchPerson(name));
        return "admin/personForMovie";
    }

    @ResponseBody
    @RequestMapping("delPersonForMovie")
    public int delPersonForMovie(int personId, int movieId, int job){
        return personService.deletePersonForMovie(personId, movieId, job);
    }

    @RequestMapping("searchPerson")
    public String searchPerson(Model model,String name){
        model.addAttribute("personList",personService.searchPerson(name));
        model.addAttribute("name",name);
        return "admin/personForMovie::personListSpace";
    }

    @RequestMapping("addPerson")
    public String addPerson(){
        return "admin/addPerson";
    }

    @RequestMapping("updatePerson")
    public String updatePerson(Model model,int id){
        model.addAttribute("person",personService.getBaseMapper().selectById(id));
        return "admin/updatePerson";
    }

    @ResponseBody
    @RequestMapping("toUpdatePerson")
    public int toUpdatePerson(int id,String name, Boolean sex, String born,String info,String engName,String country){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(born, fmt);
        Person person=new Person(id,name,sex,date,engName,info,country);
        return personService.getBaseMapper().updateById(person);
    }

    @ResponseBody
    @RequestMapping("upMoviePic")
    public String uploadHead(MultipartFile file,int movieId){
        // 要上传的目标文件存放路径
        String localPath = path;
        String fileName= FileNameUtils.getMovieFileName(file.getOriginalFilename(),movieId);
        System.out.println(fileName);
        String msg = "";

        if (FileUtils.upload(file, localPath, fileName)){
            // 上传成功，给出页面提示
            msg = "上传成功！";
        }else {
            msg = "上传失败！";

        }
        return msg;
    }


}
