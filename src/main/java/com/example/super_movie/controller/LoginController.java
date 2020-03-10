package com.example.super_movie.controller;

import com.example.super_movie.annotations.JwtIgnore;
import com.example.super_movie.config.JwtParam;
import com.example.super_movie.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Controller
public class LoginController {

    @Autowired
    private JwtParam jwtParam;

    // 登录
//    @ResponseBody
    @PostMapping("/loginJWT")
    @JwtIgnore // 加此注解, 请求不做token验证
    public String login(HttpServletResponse response) {
        // 1.用户密码验证我这里忽略, 假设用户验证成功, 取得用户id为5
        Integer userId = 53543535;

        // 2.验证通过生成token
        String token = JwtUtils.createToken(userId + "", jwtParam);
        if (token == null) {
            log.error("===== 用户签名失败 =====");
            return null;
        }
        log.info("===== 用户{}生成l签名{} =====", userId, token);
        Cookie cookie=new Cookie("token","Bearer-"+token);
        response.addCookie(cookie);
//        return JwtUtils.getAuthorizationHeader(token);
        return "yes";
    }

    // 验证
//    @ResponseBody
    @PostMapping("/hahaha")
    public String hilox(HttpServletRequest request) {
        System.out.println(123345577);
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null)
//            for(Cookie c : cookies){
//                String name = c.getName();//获取Cookie名称
//                if("token".equals(name)){
//                    String value = c.getValue();//获取Cookie的值
//                    System.out.println(value);
//                    System.out.println(request.getHeader("Authorization"));
//                }
//            }
        return "hhhh";
    }

    @RequestMapping("/jwtTest")
    @JwtIgnore
    public String toLogin(){
        return "jwtTest";
    }
}
