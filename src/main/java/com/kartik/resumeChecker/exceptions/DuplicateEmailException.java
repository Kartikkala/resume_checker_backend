package com.kartik.resumeChecker.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(String.format("An account with email '%s' already exists", email));
    }
}