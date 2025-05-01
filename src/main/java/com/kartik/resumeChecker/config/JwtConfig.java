package com.kartik.resumeChecker.config;

import com.kartik.authentication.jwt.config.JwtLoginConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JwtConfig {
    @Bean
    public JwtLoginConfig jwtLoginConfig()
    {
        return new JwtLoginConfig("email", List.of("email", "name"));
    }
}
