package com.example.quanlynhasach.service;

public interface AuthService {
    Object login(String email, String password);

    boolean logout(String token);

    void forgotPassword(String email);

    boolean resetPassword(String token, String newPassword);
}
