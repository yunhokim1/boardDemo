package com.example.demo.controller.cmn.login;

import com.example.demo.service.user.CustomUserDetailsService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // 로그인 페이지 (GET 요청)
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // login.html 페이지 반환
    }

    // 로그인 요청 처리 (POST 요청)
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 인증된 사용자로부터 JWT 토큰 생성
            String token = jwtUtil.generateToken(username);

            // 토큰을 모델에 추가하거나, 클라이언트에게 반환
            model.addAttribute("token", token);  // 페이지에서 사용할 수 있게 추가
            return "home";  // 로그인 후 홈 페이지로 리디렉션

        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "login";  // 로그인 실패 시 로그인 페이지로 돌아감
        }
    }
}
