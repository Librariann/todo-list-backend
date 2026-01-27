package com.librarian.todo_list.goals.dto;

import com.librarian.todo_list.goals.entity.GoalProcess;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalProcessResponse {
    
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long goalId;
    private String goalName;
    private Integer periodIndex;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer currentCount;
    private Integer targetCount;
    private Boolean isAchieved;
    private Boolean isFinalized;
    private Double progressPercentage;
    private Integer daysRemaining;
    
    public static GoalProcessResponse from(GoalProcess process) {
        if (process == null) return null;
        
        LocalDate now = LocalDate.now();
        int daysRemaining = Math.max(0, (int) now.until(process.getPeriodEnd()).getDays());
        
        double progress = 0.0;
        if (process.getGoals().getTargetCount() > 0) {
            progress = Math.min(100.0, 
                (double) process.getCurrentCount() / process.getGoals().getTargetCount() * 100);
        }
        
        return GoalProcessResponse.builder()
                .id(process.getId())
                .createdAt(process.getCreatedAt())
                .updatedAt(process.getUpdatedAt())
                .goalId(process.getGoals().getId())
                .goalName(process.getGoals().getName())
                .periodIndex(process.getPeriodIndex())
                .periodStart(process.getPeriodStart())
                .periodEnd(process.getPeriodEnd())
                .currentCount(process.getCurrentCount())
                .targetCount(process.getGoals().getTargetCount())
                .isAchieved(process.getIsAchieved())
                .isFinalized(process.getIsFinalized())
                .progressPercentage(progress)
                .daysRemaining(daysRemaining)
                .build();
    }
}