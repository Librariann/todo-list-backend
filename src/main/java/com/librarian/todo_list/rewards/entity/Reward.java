package com.librarian.todo_list.rewards.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Rewards extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Rewards.RewardsType type = RewardsType.POINT;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false, length = 50)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean discount = false;

    @Column(nullable = false)
    @Builder.Default
    private int discount_rate = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean is_active = true;

    // 사용자 상태 열거형
    public enum RewardsType {
        COUPON, POINT
    }
}
