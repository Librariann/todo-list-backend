package com.librarian.todo_list.habits.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "habits", schema = "todo_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Habits extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true, length= 255)
    private String description;

    @Column(nullable = false)
    private Integer dailyTarget;

    @Column(nullable = true, length = 20)
    private String unit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
