package com.librarian.todo_list.todos.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodosUpdateRequest implements Serializable {
    String name;
    LocalDate targetDate;
}