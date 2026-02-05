package com.librarian.todo_list.goals.event;

import com.librarian.todo_list.goals.entity.GoalProcess;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GoalCompletedEvent {
    private final User user;
    private final GoalProcess goals;
    private final LocalDate completedDate;
}