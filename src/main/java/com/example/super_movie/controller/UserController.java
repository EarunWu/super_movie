package com.example.super_movie.controller;


import com.example.super_movie.entity.User;
import com.example.super_movie.service.IUserService;
import com.example.super_movie.util.FileNameUtils;
import com.example.super_movie.util.FileUtils;
import com.example.super_movie.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */
@Controller
public class UserController{
    @Autowired
    IUserService userService;

    @Value("${web.upload-path}")
    private String path;

    @ResponseBody
    @RequestMapping("updateUserInfo")
    public int updateUserInfo(Model model,String username,String introduction,HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        return userService.updateUserInfo(username,introduction,userId);
    }

    @ResponseBody
    @RequestMapping("updatePassword")
    public int updatePassword(Model model,String newPassword,String password,HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        //判断密码是否为空
        if (newPassword.length()==0||password.length()==0)
            return -1;
        return userService.updatePassword(newPassword,userId,password);
    }

    @ResponseBody
    @RequestMapping("follow")
    public int follow(int followId,HttpServletRequest request){
        Integer userId=(Integer) request.getSession().getAttribute("userId");
        if (userId==null)
            return 2;
        return userService.follow(userId,followId);
    }

    @RequestMapping("getBlackList")
    public String getBlackList(Model model,HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        model.addAttribute("blackList",userService.getBlackList(userId));
        return "settings::blackListSpace";
    }

    @ResponseBody
    @RequestMapping("addBlackList")
    public int addBlackList(int id,HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        return userService.addBlackList(userId,id);
    }

    @ResponseBody
    @RequestMapping("removeBlackList")
    public int removeBlackList(int id,HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        return userService.removeBlackList(userId,id);
    }

    @RequestMapping("settings")
    public String settings(Model model,HttpServletRequest request){
        int userId=(int)request.getSession().getAttribute("userId");
        model.addAttribute("blackList",new ArrayList<UserInfo>());
        model.addAttribute("userInfo",userService.getUserInfoById(userId,userId));
        return "settings";
    }

    //注册
    @PostMapping("/create")
    public String create(Model model, String username, String password, String email){
        String info="";
        switch (userService.doRegister(username,password,email)){
            case -1:
                info="格式不正确";
                break;
            case 0:
                info="邮箱已被注册";
                break;
            case 1:
                info="请前往邮箱激活";
                break;
            default:info="未知错误";
        }
        model.addAttribute("information",info);
        return "registerResult";

    }

    @PostMapping("/loginIn")
    public String toLogin(HttpServletRequest request, Model model, String email, String password){
        Integer userId=userService.login(email,password);
        if (userId==null){
            return "redirect:/login";
        }
        if (userId==0){
            model.addAttribute("information", "账号状态异常");
            return "registerResult";
        }
        request.getSession().setAttribute("userId",userId);
        request.getSession().setMaxInactiveInterval(120*60);
        return "redirect:/index";

    }
    @ResponseBody
    @PostMapping("/loginSpace")
    public String toLogin1(HttpServletRequest request,  String email, String password){
        Integer userId=userService.login(email,password);
        if (userId==null){
            return "-1";
        }
        if (userId==0){
            return "0";
        }
        request.getSession().setAttribute("userId",userId);
        request.getSession().setMaxInactiveInterval(120*60);
        return "1";

    }
    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/login";
    }

    //激活
    @RequestMapping("/register")
    public String register(Model model,String code){
        String a="激活成功";
        String b="激活失败，请检查相关信息";
        if(userService.activeUser(code)){
            model.addAttribute("information", a);
        }else{
            model.addAttribute("information", b);
        }
        return "registerResult";
    }
    @ResponseBody
    @RequestMapping("uploadHead")
    public String uploadHead(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        Integer userId=(Integer) request.getSession().getAttribute("userId");
        // 要上传的目标文件存放路径
        String localPath = path;
        String fileName= FileNameUtils.getHeadFileName(file.getOriginalFilename(),userId);
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
