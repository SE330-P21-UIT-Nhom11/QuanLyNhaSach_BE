package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.model.enums.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(int id);

    User createUser(User user);

    User updateUser(int id, User user);

    boolean deleteUser(int id);

    public boolean login(String email, String password);

    public User loginAndReturnUser(String email, String password);
    
    // Thêm method để lấy role của user
    Role getUserRole(String email);
    
    Optional<User> getUserByEmail(String email);
}