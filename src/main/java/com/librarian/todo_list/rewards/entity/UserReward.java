package com.librarian.todo_list.rewards.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserReward extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reward_id", nullable = false)
    private Rewards rewards;

    @Column(nullable = false)
    @Builder.Default
    private boolean isUsed = false;
}
