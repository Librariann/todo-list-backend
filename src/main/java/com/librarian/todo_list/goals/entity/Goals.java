package com.librarian.todo_list.goals.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "goals", schema = "todo_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Goals extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true, length= 255)
    private String description;

    @Column(nullable = false)
    private UserPoint.PeriodTypeStatus recurrence_type;

    @Column(nullable = false)
    @Builder.Default
    private Integer interval = 1;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer targetCount = 1;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
