package com.librarian.todo_list.habits.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HabitsRegistrationRequest {

    @NotBlank(message = "습관 이름은 필수입니다")
    @Size(max = 50, message = "습관 이름은 50자 이하여야 합니다")
    private String name;

    @Size(max = 255, message = "설명은 255자 이하여야 합니다")
    private String description;

    @NotNull(message = "일일 목표 횟수는 필수입니다")
    @Min(value = 1, message = "일일 목표는 1 이상이어야 합니다")
    private Integer dailyTarget;

    @Size(max = 20, message = "단위는 20자 이하여야 합니다")
    private String unit;
}
