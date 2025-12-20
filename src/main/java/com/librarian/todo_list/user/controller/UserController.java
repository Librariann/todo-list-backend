package com.librarian.todo_list.user.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.user.dto.UserRegistrationRequest;
import com.librarian.todo_list.user.dto.UserResponse;
import com.librarian.todo_list.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("회원가입 API 호출: username={}", request.getNickname());
        
        UserResponse userResponse = userService.registerUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "회원가입이 성공적으로 완료되었습니다."));
    }
    
    /**
     * 사용자명 중복 확인
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checknicknameAvailability(
            @PathVariable String username) {
        log.info("사용자명 중복 확인: {}", username);
        
        boolean isAvailable = !userService.isnicknameExists(username);
        String message = isAvailable ? 
                "사용 가능한 사용자명입니다." : 
                "이미 사용 중인 사용자명입니다.";
        
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }
    
    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email/{email:.+}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(
            @PathVariable String email) {
        log.info("이메일 중복 확인: {}", email);
        
        boolean isAvailable = !userService.isEmailExists(email);
        String message = isAvailable ? 
                "사용 가능한 이메일입니다." : 
                "이미 사용 중인 이메일입니다.";
        
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }
    
    /**
     * 사용자 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("사용자 조회: id={}", id);
        
        Optional<UserResponse> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(user.get(), "사용자 조회가 완료되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
        }
    }
    
    /**
     * 사용자명으로 조회
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByNickname(@PathVariable String username) {
        log.info("사용자명으로 조회: username={}", username);
        
        Optional<UserResponse> user = userService.findByNickname(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(user.get(), "사용자 조회가 완료되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
        }
    }
    
    /**
     * 모든 활성 사용자 조회
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllActiveUsers() {
        log.info("모든 활성 사용자 조회");
        
        List<UserResponse> users = userService.findAllActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "활성 사용자 목록 조회가 완료되었습니다."));
    }
    
    /**
     * 헬스 체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("OK", "사용자 서비스가 정상적으로 동작 중입니다."));
    }
}