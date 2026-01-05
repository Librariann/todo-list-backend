package com.librarian.todo_list.todos.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodosResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private Todos.TodosStatus status;
    private Integer orderIndex;
    private LocalDate targetDate;
    /**
     * Rewards 엔티티를 Response DTO로 변환하는 정적 메서드
     */
    public static TodosResponse from(Todos todos) {
        return TodosResponse.builder()
                .id(todos.getId())
                .createdAt(todos.getCreatedAt())
                .updatedAt(todos.getUpdatedAt())
                .name(todos.getName())
                .status(todos.getStatus())
                .orderIndex(todos.getOrderIndex())
                .targetDate(todos.getTargetDate())
                .build();
    }
}
