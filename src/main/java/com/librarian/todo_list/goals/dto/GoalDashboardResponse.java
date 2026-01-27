package com.librarian.todo_list.goals.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDashboardResponse {
    
    private List<GoalProcessResponse> activeGoals;
    private List<GoalStreaksResponse> activeStreaks;
    private GoalStatsResponse stats;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoalStatsResponse {
        private Integer totalActiveGoals;
        private Integer totalAchievedToday;
        private Integer totalActiveStreaks;
        private Integer longestCurrentStreak;
        private Integer maxStreakEver;
        private Integer totalStreaksActive;
    }
}