package com.librarian.todo_list.todos.event;

import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TodoCompletedEvent {
    private final User user;
    private final Todos todo;
    private final LocalDate completedDate;
}