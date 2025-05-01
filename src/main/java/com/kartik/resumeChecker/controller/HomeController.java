package com.kartik.resumeChecker.controller;

import com.kartik.authentication.annotations.Authenticate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Authenticate // my authenticator's verifying (auth) functionality
@RestController
@RequestMapping("/api/v1/")
public class HomeController {
    @RequestMapping("/")
    public String greet(HttpServletRequest request)
    {
        HashMap<String, String> user = (HashMap<String, String>)request.getAttribute("user");
        return "Hii!" + user.get("email");
    }

    @GetMapping("/hello")
    public String hello(HttpServletRequest request)
    {
        HashMap<String, String> user = (HashMap<String, String>)request.getAttribute("user");

        return "Hii! I am spring boot!" + user.get("email");
    }
}
