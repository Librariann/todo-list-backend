package com.librarian.todo_list.points.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Entity
@Table(name = "user_points", schema = "todo_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserPoint extends BaseEntity {

    @Column(nullable = false)
    @Builder.Default
    private ActionStatus action = ActionStatus.CREDIT;

    @Column(nullable = false)
    @Builder.Default
    private ReasonStatus reason = ReasonStatus.CHALLENGE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true)
    private MetaTypeStatus metaType;

    @Column(nullable = true)
    private Long metaId;

    @Column(nullable = false)
    private PeriodTypeStatus periodType;

    @Column(nullable = false)
    private String periodKey;

    @Column(nullable = false)
    private Integer point;

    public enum ActionStatus {
        CREDIT, DEBIT
    }

    public enum ReasonStatus {
        CHALLENGE, SPEND, EXPIRE, ADJUST
    }

    public enum MetaTypeStatus {
        CHALLENGE, STORE, ADMIN, NONE
        //추후 더 추가 가능..
    }
    public enum PeriodTypeStatus {
        DAILY, WEEKLY, MONTHLY
    }
}
