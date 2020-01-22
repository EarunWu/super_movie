package com.example.super_movie.config;

import com.example.super_movie.annotations.JwtIgnore;
import com.example.super_movie.constants.JwtConstant;
import com.example.super_movie.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt 拦截器
 */
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtParam jwtParam;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 忽略带JwtIgnore注解的请求, 不做后续token认证校验
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            JwtIgnore jwtIgnore = handlerMethod.getMethodAnnotation(JwtIgnore.class);
            if (jwtIgnore != null) {
                return true;
            }
        }

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

//        final String authHeader = request.getHeader(JwtConstant.AUTH_HEADER_KEY);
        final String authHeader = getToken(request);

        Cookie[] cookies = request.getCookies();
        if(cookies != null)
            for(Cookie c : cookies){
                String name = c.getName();//获取Cookie名称
                if("token".equals(name)){
                    String value = c.getValue();//获取Cookie的值

                }
            }

        if (StringUtils.isEmpty(authHeader)) {
            // TODO 这里自行抛出异常
            log.info("===== 用户未登录, 请先登录 =====");
            response.sendRedirect("/jwtTest");
            return false;
        }

        // 校验头格式校验
        if (!JwtUtils.validate(authHeader)) {
            // TODO 这里自行抛出异常
            log.info("===== token格式异常 =====");
            response.sendRedirect("/jwtTest");
            return false;
        }

        // token解析
        final String authToken = JwtUtils.getRawToken(authHeader);
        Claims claims = JwtUtils.parseToken(authToken, jwtParam.getBase64Secret());
        if (claims == null) {
            log.info("===== token解析异常 =====");
            response.sendRedirect("/jwtTest");
            return false;
        }

        // 传递所需信息
         request.setAttribute("CLAIMS", claims);
        return true;
    }
    public String getToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
            for(Cookie c : cookies){
                String name = c.getName();//获取Cookie名称
                if("token".equals(name)){
                    String value = c.getValue();//获取Cookie的值
                    return value;
                }
            }
        return null;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
