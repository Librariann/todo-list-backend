package com.librarian.todo_list.habits.event;

import com.librarian.todo_list.habits.entity.HabitLog;
import com.librarian.todo_list.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class HabitCompletedEvent {
    private final User user;
    private final HabitLog habitLog;
    private final LocalDate completedDate;
}
