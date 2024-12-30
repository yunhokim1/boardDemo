package com.example.demo.service.user;

import com.example.demo.domain.user.User;

public interface UserService {

    public String registerUser(User user);

    public boolean existsByUserId(String userId);

    public User findByUserId(String userId);
}
