package com.kartik.resumeChecker.repository;

import com.kartik.resumeChecker.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
}
