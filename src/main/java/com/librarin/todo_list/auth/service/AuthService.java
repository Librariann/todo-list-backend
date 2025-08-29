package com.librarin.todo_list.auth.service;

import com.librarin.todo_list.auth.dto.LoginRequest;
import com.librarin.todo_list.auth.dto.LoginResponse;
import com.librarin.todo_list.security.jwt.JwtProperties;
import com.librarin.todo_list.security.jwt.JwtTokenProvider;
import com.librarin.todo_list.user.entity.User;
import com.librarin.todo_list.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    
    /**
     * 로그인 - JWT 토큰 발급
     */
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("로그인 시도: username={}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + request.getUsername()));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BadCredentialsException("비활성화된 계정입니다");
        }
        
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        
        setRefreshTokenCookie(response, refreshToken);
        
        log.info("로그인 성공: username={}", user.getUsername());
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * 토큰 갱신
     */
    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        
        setRefreshTokenCookie(response, newRefreshToken);
        
        log.info("토큰 갱신 성공: username={}", user.getUsername());
        
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
    
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.getCookieSecure());
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProperties.getRefreshExpiration() / 1000));
        response.addCookie(cookie);
    }
}
