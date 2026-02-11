package com.librarian.todo_list.challenges.dto;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.rewards.entity.Rewards;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Value
public class ChallengesRegistrationRequest implements Serializable {

    @NotBlank
    @Length(min = 4, max = 50)
    String name;
    String description;
    String icon;
    @NotNull
    UserPoint.PeriodTypeStatus recurrenceType;
    Integer targetCount;
    @NotNull
    Integer dailyMaxCount;
    @NotNull
    Challenges.WorkType workType;
    @NotNull
    Integer point;
    boolean isActive;
}