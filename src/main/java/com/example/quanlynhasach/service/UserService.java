package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.model.enums.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(int id); // Trả về Optional<User> để tránh lỗi null

    User createUser(User user);

    Optional<User> updateUser(int id, User user); // Trả về Optional<User> để kiểm tra cập nhật có thành công không

    boolean deleteUser(int id);

    public boolean login(String email, String password);

    public User loginAndReturnUser(String email, String password);
    
    // Thêm method để lấy role của user
    Role getUserRole(String email);
    
    Optional<User> getUserByEmail(String email);
}