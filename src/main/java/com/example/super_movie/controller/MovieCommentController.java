package com.example.super_movie.controller;


import com.example.super_movie.service.IMovieCommentService;
import com.example.super_movie.service.IReplyOfCommentService;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.FileNameUtils;
import com.example.super_movie.util.FileUtils;
import com.example.super_movie.util.RedisUtil;
import com.example.super_movie.vo.EditorImg;
import com.example.super_movie.vo.MovieCommentInfo;
import com.example.super_movie.vo.SelectMovieList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private final ResourceLoader resourceLoader;

    @Autowired
    public MovieCommentController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    IMovieCommentService movieCommentService;

    @Autowired
    IReplyOfCommentService replyOfCommentService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IUserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("postMovieComment")
    @ResponseBody
    public int postMovieComment(String content,String title,int movieId,int score, HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        return movieCommentService.postMovieComment(userId, content,title,movieId,score);
    }

    @RequestMapping("userInfo")
    public String toUserInfo(Model model,Integer id,Integer p,Integer state,HttpServletRequest request){
        Integer userId=(Integer) request.getSession().getAttribute("userId");
        if (id==null)
            id=userId;
        if (id==null)
            return "redirect:/login";
        model.addAttribute("loginId",userId);
        if (userId==null)
            userId=0;
        if (p==null)
            p=1;
        Integer num=(Integer)redisUtil.hget("number","userComment"+id);
        int page=num==null?0:num%7>0?(num/7)+1:num/7;
        model.addAttribute("commentList",movieCommentService.getCommentListByUserId(id,p,page));
        model.addAttribute("page",page);
        model.addAttribute("pageNum",p);
        if (state!=null)
            return "userInfo::commentListSpace";
        model.addAttribute("writer",userService.getUserInfoById(id,userId));
        return "userInfo";

    }

//按热度排名获取影评列表
    @RequestMapping("/movieCommentList")
    public String toMovieList(Model model,Integer movieId,Integer state,HttpServletRequest request){
        List<ZSetOperations.TypedTuple<Object>> maxLikeList=movieCommentService.getLikeRankIdByMovieId(movieId,0,9);
        List<MovieCommentInfo> movieCommentInfoList=movieCommentService.getCommentList(maxLikeList);
        model.addAttribute("commentList",movieCommentInfoList);
        model.addAttribute("maxLikeList",maxLikeList);
        model.addAttribute("page",0);
        if (state!=null)
            return "movieCommentList::commentListSpace";
        model.addAttribute("recommend",(List<SelectMovieList>)redisTemplate.opsForList().range("recommend",0,-1));
        model.addAttribute("loginId",request.getSession().getAttribute("userId"));
        return "movieCommentList";
    }
    //按发表时间排名获取影评列表
    @RequestMapping("getCommentListAsTime")
    public String toMovieListAsTime(Model model,int movieId,int page){
        //获取影评数量并计算页数
        Integer num=(Integer)redisUtil.hget("number","movieComment"+movieId);
        int page1=num==null?0:num%7>0?(num/7)+1:num/7;
        List<MovieCommentInfo> movieCommentInfoList=movieCommentService.getCommentTimeOrderList(movieId,page,page1);
        model.addAttribute("commentList",movieCommentInfoList);
        model.addAttribute("page",page1);
        model.addAttribute("pageNum",page);
        return "movieCommentList::commentListSpace";
    }

    //获取影评页面
    @RequestMapping("movieComment")
    public String toMovieCommentPage(Model model, Integer id,Integer order,HttpServletRequest request){
        if (!redisUtil.getBit("commentState",id)){
            System.out.println("数据库不存在此数据或者被删除");
            return "redirect:/index";
        }
        Integer userId=(Integer) request.getSession().getAttribute("userId");
        model.addAttribute("loginId",userId);
        //order为null则正序
        MovieCommentInfo movieComment=movieCommentService.getMovieCommentInfoById(id);
        if (movieComment==null)
            return "index";
        //标记点赞数和用户是否点赞
        movieComment.setLike(movieCommentService.getLikeNum(movieComment.getId(),movieComment.getMovieId()));
        movieComment.setState(movieCommentService.getLikeStateById(userId==null?0:userId,id,movieComment.getCreateTime().toLocalDate()));
        model.addAttribute("movieComment",movieComment);
        model.addAttribute("p",1);
        model.addAttribute("page",replyOfCommentService.getPageNum(id));
        model.addAttribute("replyList",replyOfCommentService.getReplyOfCommentByIdAndPage(id,1,order));
        model.addAttribute("order",order);
        model.addAttribute("writer",userService.getUserInfoById(movieComment.getUserId(),userId==null?0:userId));
        return "movieComment";
    }
    @ResponseBody
    @RequestMapping("like")
    public int toLike(Model model,int commentId,int movieId,String localDate,HttpServletRequest request){
        Integer userId=(Integer) request.getSession().getAttribute("userId");
        if (userId==null)
            return 0;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        movieCommentService.like(userId,commentId,movieId,LocalDate.parse(localDate, fmt));
        return 1;
    }

    @ResponseBody
    @RequestMapping("upload")
    public EditorImg upload(@RequestParam("fileName") MultipartFile file){

        // 要上传的目标文件存放路径
        String localPath = path;
        EditorImg editorImg=new EditorImg();
        String fileName= FileNameUtils.getFileName(file.getOriginalFilename());
        System.out.println(fileName);
        String[] imgData={"/show?fileName="+fileName};
        // 上传成功或者失败的提示
        String msg = "";

        if (FileUtils.upload(file, localPath, fileName)){
            // 上传成功，给出页面提示
            editorImg.setErrno(0);
            editorImg.setData(imgData);
            msg = "上传成功！";
        }else {
            editorImg.setErrno(1);
            msg = "上传失败！";

        }

        // 显示图片

        return editorImg;
    }

    @RequestMapping("show")
    public ResponseEntity showPhotos(String fileName){

        try {
            // 由于是读取本机的文件，file是一定要加上的， path是在application配置文件中的路径
            return ResponseEntity.ok(resourceLoader.getResource("file:" + path + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @RequestMapping("/report")
    @ResponseBody
    public int report(int id,boolean state,HttpServletRequest request){
        return movieCommentService.report(state,id,(int)request.getSession().getAttribute("userId"));
    }

}
