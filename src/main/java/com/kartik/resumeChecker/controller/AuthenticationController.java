package com.kartik.resumeChecker.controller;

import com.kartik.authentication.annotations.LogIn;
import com.kartik.resumeChecker.dto.LoginRequestDTO;
import com.kartik.resumeChecker.dto.RegisterRequestDTO;
import com.kartik.resumeChecker.exceptions.DuplicateEmailException;
import com.kartik.resumeChecker.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AuthenticationController {

    private AuthService authService;

    @Autowired
    AuthenticationController(AuthService authService) {
        this.authService = authService;
    }
    @LogIn
    @PostMapping("/login") // login route with my authenticator's login functionality.
    public ResponseEntity<?> login(HttpServletRequest req, @RequestBody LoginRequestDTO request)
    {
        try {
            if(authService.login(request.email(), request.password(), req)){
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO req)
    {
        try {
            authService.register(req.email(), req.password(), req.name());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateEmailException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already in use"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
