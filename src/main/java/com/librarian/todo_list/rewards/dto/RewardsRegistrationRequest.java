package com.librarian.todo_list.rewards.dto;

import com.librarian.todo_list.rewards.entity.Rewards;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsRegistrationRequest implements Serializable {
    @NotBlank
    @Length(min = 4, max = 50)
    String name;

    @NotNull
    Rewards.RewardsType type;

    @NotNull
    Integer point;
    String description;
    Boolean discount;
    Integer discountRate;
    Boolean isActive;
}