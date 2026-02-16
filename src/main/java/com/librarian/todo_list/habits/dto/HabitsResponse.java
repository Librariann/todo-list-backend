package com.librarian.todo_list.habits.dto;

import com.librarian.todo_list.habits.entity.HabitLog;
import com.librarian.todo_list.habits.entity.HabitStreaks;
import com.librarian.todo_list.habits.entity.Habits;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class HabitsResponse {

    private Long id;
    private String name;
    private String description;
    private Integer dailyTarget;
    private String unit;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // 오늘 카운터 정보
    private Integer todayCount;
    private Boolean todayAchieved;

    // 스트릭 정보
    private Integer currentStreak;
    private Integer longestStreak;

    public static HabitsResponse from(Habits habit, HabitLog todayLog, HabitStreaks streaks) {
        return HabitsResponse.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .dailyTarget(habit.getDailyTarget())
                .unit(habit.getUnit())
                .isActive(habit.getIsActive())
                .createdAt(habit.getCreatedAt())
                .todayCount(todayLog != null ? todayLog.getCurrentCount() : 0)
                .todayAchieved(todayLog != null ? todayLog.getIsAchieved() : false)
                .currentStreak(streaks != null ? streaks.getCurrentStreak() : 0)
                .longestStreak(streaks != null ? streaks.getLongestStreak() : 0)
                .build();
    }
}
