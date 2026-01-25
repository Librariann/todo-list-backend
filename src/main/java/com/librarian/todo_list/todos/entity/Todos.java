package com.librarian.todo_list.todos.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.rewards.dto.RewardsUpdateRequest;
import com.librarian.todo_list.todos.dto.TodosUpdateRequest;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity(name = "todo_list")
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Todos extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TodosStatus status = TodosStatus.READY;

    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 1;

    @Column(nullable = false)
    private LocalDate targetDate;

    public enum TodosStatus {
        READY, PROCESS, DONE
    }

    public void update(TodosUpdateRequest request){
        if (request.getName() != null) this.name = request.getName();
        if (request.getTargetDate() != null) this.targetDate = request.getTargetDate();
    }
}
