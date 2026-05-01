package com.librarian.todo_list.summary.service;

import com.librarian.todo_list.challenges.service.UserProgressChallengesService;
import com.librarian.todo_list.points.service.UserPointService;
import com.librarian.todo_list.rewards.service.UserRewardsService;
import com.librarian.todo_list.summary.dto.UserSummaryResponse;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSummaryService {

    private final UserRewardsService userRewardsService;
    private final UserProgressChallengesService userProgressChallengesService;
    private final UserPointService userPointService;

    public UserSummaryResponse getUserSummary(User user) {
        return UserSummaryResponse.builder()
                .points(userPointService.getUserTotalPoints(user))
                .rewards(userRewardsService.getUserRewards(user))
                .achievedChallenges(userProgressChallengesService.getAchievedChallenges(user))
                .build();
    }
}
