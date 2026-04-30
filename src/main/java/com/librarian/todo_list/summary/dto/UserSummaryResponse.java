package com.librarian.todo_list.summary.dto;

import com.librarian.todo_list.challenges.dto.UserProgressChallengesRes;
import com.librarian.todo_list.rewards.dto.UserRewardsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {

    private Integer points;
    private List<UserRewardsResponse> rewards;
    private List<UserProgressChallengesRes> achievedChallenges;
}
