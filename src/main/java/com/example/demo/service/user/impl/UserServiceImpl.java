package com.example.demo.service.user.impl;

import com.example.demo.dao.user.UserRepository;
import com.example.demo.domain.user.User;
import com.example.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 비밀번호 암호화
        user.setRole("USER"); // 기본 권한 설정
        userRepository.save(user);

        return "SUCCESS";
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    // 현재 로그인된 사용자 정보 가져오기
    @Override
    public User loggedInUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return findByUserId(userId);
    }


}
