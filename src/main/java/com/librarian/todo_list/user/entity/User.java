package com.librarian.todo_list.user.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.rewards.entity.UserReward;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String nickname;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(length = 50)
    private String name;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    // 사용자 상태 열거형
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, WITHDRAWN
    }
    
    // 사용자 역할 열거형
    public enum UserRole {
        USER, ADMIN
    }
}
