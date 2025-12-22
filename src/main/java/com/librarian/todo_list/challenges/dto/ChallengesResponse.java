package com.librarian.todo_list.challenges.dto;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.rewards.entity.Rewards;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengesResponse {

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
    public static ChallengesResponse from(Challenges challenges) {
        return ChallengesResponse.builder()
                .id(challenges.getId())
                .createdAt(challenges.getCreatedAt())
                .updatedAt(challenges.getUpdatedAt())
                .name(challenges.getName())
                .description(challenges.getDescription())
                .icon(challenges.getIcon())
                .recurrenceType(challenges.getRecurrenceType())
                .targetCount(challenges.getTargetCount())
                .point(challenges.getPoint())
                .isActive(challenges.isActive())
                .build();
    }
}
