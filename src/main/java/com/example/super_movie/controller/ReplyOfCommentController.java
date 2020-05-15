package com.example.super_movie.controller;


import com.example.super_movie.service.IReplyOfCommentService;
import com.example.super_movie.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-03-01
 */
@Controller
public class ReplyOfCommentController{

    @Autowired
    IReplyOfCommentService replyOfCommentService;

    @Autowired
    RedisUtil redisUtil;
//获取评论
    @RequestMapping("/getReply")
    public String toReplyPage(int id, int page, Integer order, Model model){
        model.addAttribute("order",order);
        model.addAttribute("replyList",replyOfCommentService.getReplyOfCommentByIdAndPage(id,page,order));
        return "movieComment::replySpace";
    }

    //对影评进行评论
    @RequestMapping("/saveReply")
    @ResponseBody
    public int toSaveReply(int movieCommentId, int replyId, String content, HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        int a=replyOfCommentService.saveReply(userId, movieCommentId, replyId, content);
        redisUtil.hincr("number","commentReply"+movieCommentId,1);
        return a;
    }
}
