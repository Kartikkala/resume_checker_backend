package com.kartik.resumeChecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kartik.resumeChecker", "com.kartik.authentication"})
public class ResumeCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeCheckerApplication.class, args);
    }

}