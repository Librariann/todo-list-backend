package com.librarian.todo_list.auth.service;

import com.librarian.todo_list.auth.dto.LoginRequest;
import com.librarian.todo_list.auth.dto.LoginResponse;
import com.librarian.todo_list.security.jwt.JwtTokenProvider;
import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    
    /**
     * 로그인 - JWT 토큰 발급
     */
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 시도: email={}", request.getEmail());
        
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + request.getEmail()));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }
        
        // 사용자 상태 확인
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BadCredentialsException("비활성화된 계정입니다");
        }
        
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        
        log.info("로그인 성공: username={}", user.getEmail());
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L) // 24시간
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * 토큰 갱신
     */
    public LoginResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다");
        }
        
        // 토큰에서 사용자명 추출
        String username = jwtTokenProvider.getEmailFromToken(refreshToken);
        
        // 사용자 조회
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        
        log.info("토큰 갱신 성공: username={}", user.getNickname());
        
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
