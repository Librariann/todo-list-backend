package com.librarian.todo_list.todos.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.security.CustomUserDetails;
import com.librarian.todo_list.todos.dto.TodosRegistrationRequest;
import com.librarian.todo_list.todos.dto.TodosResponse;
import com.librarian.todo_list.todos.dto.TodosUpdateRequest;
import com.librarian.todo_list.todos.service.TodosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodosController {
    private final TodosService todosService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<TodosResponse>>> getTodosList() {
        log.info("할 일 목록 API 호출");

        List<TodosResponse> todosResponse = todosService.getTodos();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(todosResponse, "할 일 목록을 성공적으로 불러왔습니다."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TodosResponse>> registerTodos(
            @Valid @RequestBody TodosRegistrationRequest request,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("할 일 등록 API 호출: name={}", request.getName());

        TodosResponse todosResponse = todosService.registerTodos(request, principal.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(todosResponse, "할 일이 성공적으로 등록 완료되었습니다."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTodos(
            @Valid @RequestBody TodosUpdateRequest request,
            @PathVariable Long id,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("할 일 수정 API 호출: name={}", request.getName());

        TodosResponse todosResponse = todosService.updateTodos(request, id, principal.getUser());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(todosResponse, "할 일이 성공적으로 수정 완료되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTodos(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("할 일 삭제 API 호출: id={}", id);

        todosService.deleteTodos(id, principal.getUser());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(null,"할 일이 성공적으로 삭제 완료되었습니다."));
    }
}
