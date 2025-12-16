package com.librarian.todo_list.rewards.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Value
public class RewardsUpdateRequest implements Serializable {
    @Length(min = 4, max = 50)
    String name;
    Rewards.RewardsType type;
    Integer point;
    String description;
    Boolean discount;
    Integer discountRate;
}