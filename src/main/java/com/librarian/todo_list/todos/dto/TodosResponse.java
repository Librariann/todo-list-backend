package com.librarian.todo_list.todos.dto;

import com.librarian.todo_list.todos.entity.Todos;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "할 일 응답 데이터")
public class TodosResponse {

    @Schema(description = "할 일 ID", example = "1")
    private Long id;
    
    @Schema(description = "생성 일시", example = "2026-01-27T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 일시", example = "2026-01-27T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "할 일 제목", example = "프로젝트 문서 작성")
    private String name;
    
    @Schema(description = "할 일 상태", example = "READY", allowableValues = {"READY", "PROCESS", "DONE"})
    private Todos.TodosStatus status;
    
    @Schema(description = "정렬 순서", example = "1")
    private Integer orderIndex;
    
    @Schema(description = "목표 완료 날짜", example = "2026-01-27")
    private LocalDate targetDate;

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
