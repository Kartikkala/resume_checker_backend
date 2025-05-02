package com.kartik.resumeChecker.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<?> onError(HttpServletRequest req)
    {
        String clientIp = req.getHeader("X-Forwarded-For");
        if(clientIp == null || clientIp.isEmpty())
        {
            clientIp = req.getHeader("X-Real-IP");
            System.out.println("Real IP");
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = req.getRemoteAddr();
            System.out.println("GetRemoteAddr()");
        }
        System.out.println("Ip Addr: "+clientIp);
        System.out.println(req.getRemoteAddr());
        return ResponseEntity.status(404).body("Something went wrong!");
    }
}
