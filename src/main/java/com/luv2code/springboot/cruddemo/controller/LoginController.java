package com.luv2code.springboot.cruddemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/loginPage")
    public String getLoginPage() {
        return "login-page";
    }
}
