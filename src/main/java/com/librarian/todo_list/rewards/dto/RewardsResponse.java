package com.librarian.todo_list.rewards.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.user.dto.UserResponse;
import com.librarian.todo_list.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private Rewards.RewardsType type;
    private Integer point;
    private String description;
    private Boolean discount;
    private Integer discountRate;
    private Boolean isActive;

    /**
     * Rewards 엔티티를 Response DTO로 변환하는 정적 메서드
     */
    public static RewardsResponse from(Rewards rewards) {
        return RewardsResponse.builder()
                .id(rewards.getId())
                .createdAt(rewards.getCreatedAt())
                .updatedAt(rewards.getUpdatedAt())
                .name(rewards.getName())
                .type(rewards.getType())
                .point(rewards.getPoint())
                .description(rewards.getDescription())
                .discount(rewards.isDiscount())
                .discountRate(rewards.getDiscountRate())
                .isActive(rewards.isActive())
                .build();
    }
}
