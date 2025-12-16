package com.librarian.todo_list.auth.controller;

import com.librarian.todo_list.auth.dto.LoginRequest;
import com.librarian.todo_list.auth.dto.LoginResponse;
import com.librarian.todo_list.auth.service.AuthService;
import com.librarian.todo_list.common.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 API 호출: username={}", request.getEmail());
        
        LoginResponse loginResponse = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "로그인에 성공했습니다."));
    }
    
    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        log.info("토큰 갱신 API 호출");
        
        // Bearer 제거
        String refreshToken = authHeader.replace("Bearer ", "");
        
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "토큰이 갱신되었습니다."));
    }
    
    /**
     * 로그아웃 (클라이언트 측에서 토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        log.info("로그아웃 API 호출");
        
        // JWT는 stateless하므로 서버에서 별도 처리 불필요
        // 클라이언트에서 토큰을 삭제하면 됨
        
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다.", "로그아웃에 성공했습니다."));
    }
}
