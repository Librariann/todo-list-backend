package com.librarian.todo_list.points.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPointsInputRequest {

    @NotNull(message = "아이디는 필수입니다.")
    private Long id;

    @NotNull(message = "입력 포인트는 필수입니다.")
    private Integer point;
}
