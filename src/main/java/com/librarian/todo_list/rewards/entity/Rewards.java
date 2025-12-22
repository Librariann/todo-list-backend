package com.librarian.todo_list.rewards.entity;

import com.librarian.todo_list.common.entity.BaseEntity;
import com.librarian.todo_list.rewards.dto.RewardsUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Rewards extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Rewards.RewardsType type = RewardsType.POINT;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false, length = 50)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean discount = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer discountRate = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    // 사용자 상태 열거형
    public enum RewardsType {
        COUPON, POINT
    }

    public void update(RewardsUpdateRequest request){
        if (request.getName() != null) this.name = request.getName();
        if (request.getType() != null) this.type = request.getType();
        if (request.getPoint() != null) this.point = request.getPoint();
        if (request.getDescription() != null) this.description = request.getDescription();
        if (request.getDiscount() != null) this.discount = request.getDiscount();
        if (request.getDiscountRate() != null) this.discountRate = request.getDiscountRate();
    }
}
