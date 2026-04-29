package com.librarian.todo_list.rewards.service;

import com.librarian.todo_list.rewards.dto.UserRewardsResponse;
import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.rewards.entity.UserReward;
import com.librarian.todo_list.rewards.repository.RewardsRepository;
import com.librarian.todo_list.rewards.repository.UserRewardsRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRewardsService {

    private final UserRewardsRepository userRewardsRepository;
    private final RewardsRepository rewardsRepository;
    public List<UserRewardsResponse> getUserRewards(User user) {
        return userRewardsRepository.findByUser(user)
                .stream()
                .map(UserRewardsResponse::from)
                .toList();
    }

    @Transactional
    public UserRewardsResponse redeemUserRewards(Long rewardId , User user) {

        Rewards reward = rewardsRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다: " + rewardId));


        // create Rewards Entity
        UserReward userRewards = UserReward.builder()
                .user(user)
                .rewards(reward)
                .rewardName(reward.getName())
                .rewardType(reward.getType())
                .rewardPoint(reward.getPoint())
                .rewardDescription(reward.getDescription())
                .discount(reward.isDiscount())
                .discountRate(reward.getDiscountRate())
                .build();

        // 사용자 저장
        UserReward savedRewards = userRewardsRepository.save(userRewards);
        return UserRewardsResponse.from(savedRewards);
    }

    // 수정
    @Transactional
    public UserRewardsResponse useUserRewards(Long id) {
        UserReward getUserReward = userRewardsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다: " + id));

        if(!getUserReward.isUsed()){
            return UserRewardsResponse.from(getUserReward);
        }
        getUserReward.setUsed(true);

        return UserRewardsResponse.from(getUserReward);
    }
}
