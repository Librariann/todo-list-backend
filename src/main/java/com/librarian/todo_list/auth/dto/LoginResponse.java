package com.librarian.todo_list.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 응답 데이터")
public class LoginResponse {
    
    @Schema(description = "JWT 접근 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "JWT 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";
    
    @Schema(description = "토큰 만료 시간 (밀리초)", example = "86400000")
    private Long expiresIn;
    
    @Schema(description = "사용자 닉네임", example = "testuser")
    private String nickname;
    
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;
}
