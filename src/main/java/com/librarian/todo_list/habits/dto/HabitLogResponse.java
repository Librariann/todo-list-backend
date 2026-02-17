package com.librarian.todo_list.habits.dto;

import com.librarian.todo_list.habits.entity.HabitLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HabitLogResponse {

    private Long id;
    private Long habitId;
    private LocalDate logDate;
    private Integer currentCount;
    private Boolean isAchieved;

    public static HabitLogResponse from(HabitLog log) {
        return HabitLogResponse.builder()
                .id(log.getId())
                .habitId(log.getHabit().getId())
                .logDate(log.getLogDate())
                .currentCount(log.getCurrentCount())
                .isAchieved(log.getIsAchieved())
                .build();
    }
}
