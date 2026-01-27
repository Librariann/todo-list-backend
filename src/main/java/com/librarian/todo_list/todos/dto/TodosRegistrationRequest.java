package com.librarian.todo_list.todos.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodosRegistrationRequest implements Serializable {
    @Schema(description = "할 일 제목", example = "프로젝트 문서 작성", required = true, minLength = 4, maxLength = 50)
    @NotBlank
    @Length(min = 4, max = 50)
    String name;

    @Schema(description = "목표 완료 날짜", example = "2026-01-27", required = true)
    @NotNull
    LocalDate targetDate;
}