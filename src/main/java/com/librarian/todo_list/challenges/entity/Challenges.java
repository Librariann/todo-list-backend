package com.librarian.todo_list.challenges.entity;

import com.librarian.todo_list.challenges.dto.ChallengesUpdateRequest;
import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.rewards.dto.RewardsUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "challenges", schema = "todo_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Challenges extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserPoint.PeriodTypeStatus recurrenceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @Column(nullable = false)
    private Integer targetCount;

    @Column(nullable = false)
    private Integer dailyMaxCount;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = false;

    public enum WorkType {
        HABITS, TODOS, GOALS
    }

    public void update(ChallengesUpdateRequest request){
        if (request.getName() != null) this.name = request.getName();
        if (request.getPoint() != null) this.point = request.getPoint();
        if (request.getDescription() != null) this.description = request.getDescription();
        if (request.getIcon() != null) this.icon = request.getIcon();
        if (request.getTargetCount() != null) this.targetCount = request.getTargetCount();
        if (request.getRecurrenceType() != null) this.recurrenceType = request.getRecurrenceType();
    }
}
