package com.example.super_movie.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
//就是拦截器
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }
    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }
    @Override
    public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
        //普通路径放行
        if ("/index".equals(arg0.getRequestURI()) || "/login".equals(arg0.getRequestURI()) || "/movieInfo".equals(arg0.getRequestURI()) || "/movieComment".equals(arg0.getRequestURI()) || "/movieCommentList".equals(arg0.getRequestURI()) || "/getCommentListAsTime".equals(arg0.getRequestURI()) || "/show".equals(arg0.getRequestURI()) || "/loginIn".equals(arg0.getRequestURI()) || "/like".equals(arg0.getRequestURI()) || "/create".equals(arg0.getRequestURI()) || "/loginSpace".equals(arg0.getRequestURI()) || "/userInfo".equals(arg0.getRequestURI()) || "/movieSelect".equals(arg0.getRequestURI()) || "/movieRank".equals(arg0.getRequestURI())) {
            return true;}
        //权限路径拦截
        if ("/admin/*".equals(arg0.getRequestURI()))
            return true;
        Object object = arg0.getSession().getAttribute("userId");
        if (null == object) {
            arg1.sendRedirect("/login");
            return false;}
        return true;
    }
}
