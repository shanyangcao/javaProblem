package com.shuxuejia.managementsystem.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Files;

/*
   @auth0r  chagumu
    
*/
@Controller
public class loginController {
    @RequestMapping("/user/login")
    public String login(@RequestParam("loginUsername") String username, @RequestParam("loginPassword") String password, Model model, HttpSession session) {
        //如果用户名和密码正确
        if ("@1".equals(username) && "1234".equals(password)) {
            session.setAttribute("LoginUser", username);
            return "redirect:/index.html";//跳转到index页面
            //如果用户名或者密码不正确
        }
        else {
            model.addAttribute("msg", "用户名或者密码错误");//显示错误信息
            return "login";//跳转到登录页
        }
    }

    @RequestMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html";
    }
}
