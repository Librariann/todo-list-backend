package com.librarian.todo_list.rewards.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.rewards.entity.UserReward;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRewardsResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private Rewards.RewardsType type;
    private Integer point;
    private String description;
    private boolean discount;
    private Integer discountRate;
    private boolean isUsed;

    public static UserRewardsResponse from(UserReward userReward) {
        return UserRewardsResponse.builder()
                .id(userReward.getId())
                .createdAt(userReward.getCreatedAt())
                .updatedAt(userReward.getUpdatedAt())
                .name(userReward.getRewardName())
                .type(userReward.getRewardType())
                .point(userReward.getRewardPoint())
                .description(userReward.getRewardDescription())
                .discount(userReward.isDiscount())
                .discountRate(userReward.getDiscountRate())
                .isUsed(userReward.isUsed())
                .build();
    }
}
