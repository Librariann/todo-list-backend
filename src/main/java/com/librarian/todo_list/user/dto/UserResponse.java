package com.librarian.todo_list.user.dto;

import com.librarian.todo_list.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 응답 데이터")
public class UserResponse {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    
    @Schema(description = "사용자 닉네임", example = "testuser")
    private String nickname;
    
    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;
    
    @Schema(description = "사용자 실명", example = "홍길동")
    private String name;
    
    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;
    
    @Schema(description = "사용자 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private User.UserStatus status;
    
    @Schema(description = "사용자 역할", example = "USER", allowableValues = {"USER", "ADMIN"})
    private User.UserRole role;
    
    @Schema(description = "생성 일시", example = "2026-01-27T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 일시", example = "2026-01-27T10:30:00")
    private LocalDateTime updatedAt;
    
    /**
     * User 엔티티를 UserResponse DTO로 변환하는 정적 메서드
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}