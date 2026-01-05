package com.librarian.todo_list.rewards.service;

import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.rewards.dto.RewardsRegistrationRequest;
import com.librarian.todo_list.rewards.dto.RewardsResponse;
import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.rewards.repository.RewardsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardsService {

    private final RewardsRepository rewardsRepository;

    //get list
    public List<RewardsResponse> getRewards() {
        return rewardsRepository.findByIsActiveTrue()
                .stream()
                .map(RewardsResponse::from)
                .toList();
    }

    //get list One
    public RewardsResponse getOneReward(Long id) {
        RewardsResponse rewards = rewardsRepository.findById(id)
                .map(RewardsResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다."));

        if(!rewards.getIsActive()){
            throw new IllegalArgumentException("삭제된 보상 입니다.");
        }

        return rewards;
    }

    @Transactional
    public RewardsResponse registerRewards(RewardsRegistrationRequest request) {
        log.info("보상 목록 등록 요청: name={}", request.getName());

        // 중복 보상확인
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
        Rewards savedRewards = rewardsRepository.save(rewards);
        return RewardsResponse.from(savedRewards);
    }

    // 수정
//    public RewardsResponse updateRewards(RewardsRegistrationRequest request, Long id) {
//
//    }

    // 삭제
    @Transactional
    public RewardsResponse deleteRewards(Long id) {
        Rewards getReward = rewardsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다: " + id));
        if(!getReward.isActive()){
            return RewardsResponse.from(getReward);
        }
        getReward.setActive(false);
        return RewardsResponse.from(getReward);

    }
    /**
     * 이미 사용중인 Reward 확인
     */
    private void validateRewardsUniqueness(String name) {
        if (rewardsRepository.existsByName(name)) {
            throw new CommonAlreadyExistsException("이미 사용중인 보상명 입니다: " + name);
        }
    }
}
