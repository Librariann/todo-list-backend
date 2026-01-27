package com.librarian.todo_list.auth.controller;

import com.librarian.todo_list.auth.dto.LoginRequest;
import com.librarian.todo_list.auth.dto.LoginResponse;
import com.librarian.todo_list.auth.service.AuthService;
import com.librarian.todo_list.auth.service.SessionService;
import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "로그인, 로그아웃, 토큰 갱신 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final SessionService sessionService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 API 호출: username={}", request.getEmail());
        
        LoginResponse loginResponse = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "로그인에 성공했습니다."));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
        @RequestHeader("Authorization") String authHeader) {
        log.info("토큰 갱신 API 호출");
        
        // Bearer 제거
        String refreshToken = authHeader.replace("Bearer ", "");
        
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "토큰이 갱신되었습니다."));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        log.info("로그아웃 API 호출");
        
        try {
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtTokenProvider.validateToken(token)) {
                    String sessionId = jwtTokenProvider.getSessionIdFromToken(token);
                    
                    if (sessionId != null) {
                        sessionService.invalidateSession(sessionId);
                        log.info("세션 무효화 완료: sessionId={}", sessionId);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("로그아웃 중 오류 발생: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다.", "로그아웃에 성공했습니다."));
    }
}
