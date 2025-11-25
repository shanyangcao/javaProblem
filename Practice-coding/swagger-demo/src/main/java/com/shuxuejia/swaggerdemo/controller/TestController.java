package com.shuxuejia.swaggerdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
   @auth0r  chagumu
    
*/
@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test() {
        return "Test API is working!";
    }
}
