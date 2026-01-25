package com.librarian.todo_list.challenges.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_progress_challenges", schema = "todo_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserProgressChallenges extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenges_id", nullable = false)
    private Challenges challenges;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private UserPoint.PeriodTypeStatus periodType;

    @Column(nullable = false)
    private String periodKey;

    @Column(nullable = false)
    private Integer currentCount;

    @Column(nullable = false)
    private Boolean isAchieved;
}
