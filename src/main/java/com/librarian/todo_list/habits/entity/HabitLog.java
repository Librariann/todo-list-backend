package com.librarian.todo_list.habits.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "habit_logs", schema = "todo_list",
        uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "log_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HabitLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habits habit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAchieved = false;
}
