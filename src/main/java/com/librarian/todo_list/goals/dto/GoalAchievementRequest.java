package com.librarian.todo_list.goals.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

@Value
public class GoalAchievementRequest implements Serializable {
    
    @NotNull
    Long goalId;
}