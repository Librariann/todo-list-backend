package com.librarian.todo_list.user.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.security.CustomUserDetails;
import com.librarian.todo_list.user.dto.UserRegistrationRequest;
import com.librarian.todo_list.user.dto.UserResponse;
import com.librarian.todo_list.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "사용자 관리", description = "회원가입, 사용자 조회, 중복 확인 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("회원가입 API 호출: username={}", request.getNickname());

        UserResponse userResponse = userService.registerUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "회원가입이 성공적으로 완료되었습니다."));
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checknicknameAvailability(
            @PathVariable String username) {
        log.info("사용자명 중복 확인: {}", username);
        
        boolean isAvailable = !userService.isNicknameExists(username);
        String message = isAvailable ? 
                "사용 가능한 사용자명입니다." : 
                "이미 사용 중인 사용자명입니다.";
        
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }
    
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
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllActiveUsers() {
        log.info("모든 활성 사용자 조회");
        
        List<UserResponse> users = userService.findAllActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "활성 사용자 목록 조회가 완료되었습니다."));
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("OK", "사용자 서비스가 정상적으로 동작 중입니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(principal.getUser()), "내 정보 조회"));
    }
}