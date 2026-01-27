package com.librarian.todo_list.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    @NotBlank(message = "email은 필수입니다")
    private String email;
    
    @Schema(description = "사용자 비밀번호", example = "password123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
