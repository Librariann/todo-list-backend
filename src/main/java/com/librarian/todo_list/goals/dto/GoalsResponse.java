package com.librarian.todo_list.goals.dto;

import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.todos.entity.Todos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalsResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String description;
    private UserPoint.PeriodTypeStatus recurrenceType;
    private Integer interval;
    private LocalDate startDate;
    private Integer targetCount;

    public static GoalsResponse from(Goals goals) {
        return GoalsResponse.builder()
                .id(goals.getId())
                .createdAt(goals.getCreatedAt())
                .updatedAt(goals.getUpdatedAt())
                .name(goals.getName())
                .description(goals.getDescription())
                .recurrenceType(goals.getRecurrenceType())
                .interval(goals.getInterval())
                .startDate(goals.getStartDate())
                .targetCount(goals.getTargetCount())
                .build();
    }
}
