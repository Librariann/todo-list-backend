package com.librarian.todo_list.challenges.service;

import com.librarian.todo_list.challenges.dto.ChallengesResponse;
import com.librarian.todo_list.challenges.repository.ChallengesRepository;
import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.rewards.dto.RewardsRegistrationRequest;
import com.librarian.todo_list.rewards.dto.RewardsResponse;
import com.librarian.todo_list.rewards.dto.RewardsUpdateRequest;
import com.librarian.todo_list.rewards.entity.Rewards;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengesService {

    private final ChallengesRepository challengesRepository;

    //get list
    public List<ChallengesResponse> getChallenges() {
        return challengesRepository.findByIsActiveTrue()
                .stream()
                .map(ChallengesResponse::from)
                .toList();
    }

    //get list One
    public ChallengesResponse getOneChallenges(Long id) {
        ChallengesResponse rewards = challengesRepository.findById(id)
                .map(ChallengesResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다."));

        if(!rewards.getIsActive()){
            throw new IllegalArgumentException("삭제된 보상 입니다.");
        }

        return rewards;
    }

    @Transactional
    public RewardsResponse registerChallenges(RewardsRegistrationRequest request) {
        // 중복 보상명 확인
        validateRewardsUniqueness(request.getName());

        // create Rewards Entity
        Rewards rewards = Rewards.builder()
                .name(request.getName())
                .type(request.getType())
                .point(request.getPoint())
                .description(request.getDescription())
                .discount(request.getDiscount())
                .discountRate(request.getDiscountRate())
                .isActive(request.getIsActive())
                .build();

        // 사용자 저장
        Rewards savedRewards = challengesRepository.save(rewards);
        return RewardsResponse.from(savedRewards);
    }

    // 수정
    @Transactional
    public RewardsResponse updateChallenges(RewardsUpdateRequest request, Long id) {
        Rewards getReward = challengesRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다: " + id));

        if(request.getName() != null
                && !request.getName().isBlank()
                && challengesRepository.existsByNameAndIsActiveTrueAndIdNot(request.getName(), id)) {
            // 중복 보상명 확인
            throw new CommonAlreadyExistsException("이미 사용중인 보상명 입니다: " + request.getName());
        }

        getReward.update(request);

        return RewardsResponse.from(getReward);
    }

    // 삭제
    @Transactional
    public RewardsResponse deleteRewards(Long id) {
        Rewards getReward = challengesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다: " + id));

        if(!getReward.isActive()){
            return RewardsResponse.from(getReward);
        }
        getReward.setActive(false);

        return RewardsResponse.from(getReward);

    }
    /**
     * 이미 사용중인 Reward 확인 (전체)
     */
    private void validateRewardsUniqueness(String name) {
        if (challengesRepository.existsByName(name)) {
            throw new CommonAlreadyExistsException("이미 사용중인 보상명 입니다: " + name);
        }
    }
}
