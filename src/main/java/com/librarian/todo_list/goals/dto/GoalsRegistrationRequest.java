package com.librarian.todo_list.goals.dto;

import com.librarian.todo_list.points.entity.UserPoint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@Value
public class GoalsRegistrationRequest implements Serializable {

    @NotBlank
    @Length(min = 4, max = 50)
    String name;
    String description;
    @NotNull
    UserPoint.PeriodTypeStatus recurrenceType;
    @NotNull
    Integer interval;
    @NotNull
    LocalDate startDate;
    @NotNull
    Integer targetCount;
    boolean isActive;
}