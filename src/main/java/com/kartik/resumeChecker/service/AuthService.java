package com.kartik.resumeChecker.service;

import com.kartik.resumeChecker.exceptions.DuplicateEmailException;
import com.kartik.resumeChecker.model.AppUser;
import com.kartik.resumeChecker.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    AuthService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Boolean login(String email, String password, HttpServletRequest request)
    {
        AppUser user = userRepository.findByEmail(email);
        if(user != null)
        {
            if(encoder.matches(password, user.getPasswordHash())){
                HashMap<String, Object> userDetails = new HashMap<>();
                userDetails.put("name", user.getName());
                userDetails.put("email", user.getEmail());
                request.setAttribute("user", userDetails);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean register(String email, String password, String name)
    {
        AppUser user = userRepository.findByEmail(email);
        if(user != null)
        {
            throw new DuplicateEmailException("Email already in use!");
        }
        if (email == null || password == null || name == null ||
                email.isBlank() || password.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException("Email, password, and name must be provided");
        }
        AppUser newUser = new AppUser();
        newUser.setEmail(email);
        newUser.setPasswordHash(encoder.encode(password));
        newUser.setName(name);
        userRepository.save(newUser);
        return true;
    }
}
