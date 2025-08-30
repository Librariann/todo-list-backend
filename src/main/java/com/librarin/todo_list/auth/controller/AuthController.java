package com.librarin.todo_list.auth.controller;

import com.librarin.todo_list.auth.dto.LoginRequest;
import com.librarin.todo_list.auth.dto.LoginResponse;
import com.librarin.todo_list.auth.service.AuthService;
import com.librarin.todo_list.user.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        log.info("로그인 API 호출: username={}", request.getUsername());
        
        LoginResponse loginResponse = authService.login(request, response);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "로그인에 성공했습니다."));
    }
    
    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info("토큰 갱신 API 호출");
        
        String refreshToken = getRefreshTokenFromCookie(request);
        
        LoginResponse loginResponse = authService.refreshToken(refreshToken, response);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "토큰이 갱신되었습니다."));
    }
    
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new IllegalArgumentException("리프레시 토큰을 찾을 수 없습니다");
    }
    
    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        log.info("로그아웃 API 호출");
        
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다.", "로그아웃에 성공했습니다."));
    }
}
