package com.librarian.todo_list.goals.dto;

import com.librarian.todo_list.goals.entity.GoalStreaks;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalStreaksResponse {
    
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long goalId;
    private String goalName;
    private Integer currentStreak;
    private Integer longestStreak;
    private Boolean isActive;
    
    public static GoalStreaksResponse from(GoalStreaks streaks) {
        if (streaks == null) return null;
        
        return GoalStreaksResponse.builder()
                .id(streaks.getId())
                .createdAt(streaks.getCreatedAt())
                .updatedAt(streaks.getUpdatedAt())
                .goalId(streaks.getGoals().getId())
                .goalName(streaks.getGoals().getName())
                .currentStreak(streaks.getCurrentStreak())
                .longestStreak(streaks.getLongestStreak())
                .isActive(streaks.getCurrentStreak() > 0)
                .build();
    }
}