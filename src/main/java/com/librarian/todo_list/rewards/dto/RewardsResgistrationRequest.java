package com.librarian.todo_list.rewards.dto;

import com.librarian.todo_list.rewards.entity.Reward;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * DTO for {@link com.librarian.todo_list.rewards.entity.Reward}
 */
@Value
public class RewardResgistrationRequest implements Serializable {
    @NotNull
    @NotEmpty
    @NotBlank
    @Length(min = 4, max = 50)
    String name;

    @NotNull
    @NotEmpty
    @NotBlank
    Reward.RewardsType type;

    @NotNull
    @NotEmpty
    @NotBlank
    Integer point;
    String description;
    Boolean discount;
    Integer discountRate;
    Boolean isActive;
}