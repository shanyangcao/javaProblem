package com.shuxuejia.managementsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/*
   @auth0r  chagumu
    
*/public class LoginHandlerInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //用户登录成功后,应该有自己的session
        Object session = request.getSession().getAttribute("LoginUser");
        if (session == null) {
            request.setAttribute("msg", "权限不够,请先登录");
            request.getRequestDispatcher("/login.html").forward(request, response);
            return false;
        } else {
            return true;
        }
    }
}
