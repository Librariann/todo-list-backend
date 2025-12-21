package com.librarian.todo_list.user.dto;

import com.librarian.todo_list.user.entity.User;
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
public class UserResponse {
    
    private Long id;
    private String nickname;
    private String email;
    private String name;
    private String phoneNumber;
    private User.UserStatus status;
    private User.UserRole role;
    private LocalDateTime createdAt;
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