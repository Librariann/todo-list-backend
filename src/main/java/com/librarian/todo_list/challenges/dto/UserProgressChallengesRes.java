package com.librarian.todo_list.challenges.dto;

import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.points.entity.UserPoint;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressChallengesRes {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String description;
    private String icon;
    private UserPoint.PeriodTypeStatus recurrenceType;
    private Integer targetCount;
    private Integer point;
    private Boolean isActive;
    /**
     * Rewards 엔티티를 Response DTO로 변환하는 정적 메서드
     */
    public static UserProgressChallengesRes from(UserProgressChallenges progress) {
        return UserProgressChallengesRes.builder()
                .id(progress.getId())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
