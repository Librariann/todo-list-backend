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

    public static UserRewardsResponse from(UserReward userReward) {
        return UserRewardsResponse.builder()
                .id(userReward.getId())
                .createdAt(userReward.getCreatedAt())
                .updatedAt(userReward.getUpdatedAt())
                .build();
    }
}
