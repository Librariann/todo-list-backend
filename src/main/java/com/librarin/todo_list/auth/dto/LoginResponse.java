package com.librarin.todo_list.auth.dto;

import lombok.*;

/**
 * 로그인 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String username;
    private String email;
}
