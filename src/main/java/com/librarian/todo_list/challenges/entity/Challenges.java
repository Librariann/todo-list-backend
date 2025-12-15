package com.librarian.todo_list.challenges.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.points.entity.UserPoint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Challenge extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private UserPoint.PeriodTypeStatus recurrenceType;

    @Column(nullable = false)
    private Integer targetCount;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = false;
}
