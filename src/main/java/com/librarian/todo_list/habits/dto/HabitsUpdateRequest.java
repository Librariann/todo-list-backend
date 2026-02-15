package com.librarian.todo_list.habits.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HabitsUpdateRequest {

    @Size(max = 50, message = "습관 이름은 50자 이하여야 합니다")
    private String name;

    @Size(max = 255, message = "설명은 255자 이하여야 합니다")
    private String description;

    @Min(value = 1, message = "일일 목표는 1 이상이어야 합니다")
    private Integer dailyTarget;

    @Size(max = 20, message = "단위는 20자 이하여야 합니다")
    private String unit;
}
