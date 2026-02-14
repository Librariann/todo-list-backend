package com.librarian.todo_list.challenges.service;

import com.librarian.todo_list.challenges.dto.ChallengesRegistrationRequest;
import com.librarian.todo_list.challenges.dto.ChallengesResponse;
import com.librarian.todo_list.challenges.dto.ChallengesUpdateRequest;
import com.librarian.todo_list.challenges.entity.Challenges;
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
        ChallengesResponse challenges = challengesRepository.findById(id)
                .map(ChallengesResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다."));

        if(!challenges.getIsActive()){
            throw new IllegalArgumentException("삭제된 보상 입니다.");
        }

        return challenges;
    }

    @Transactional
    public ChallengesResponse registerChallenges(ChallengesRegistrationRequest request) {
        // 중복 보상명 확인
        validateRewardsUniqueness(request.getName());

        // create Rewards Entity
        Challenges rewards = Challenges.builder()
                .name(request.getName())
                .recurrenceType(request.getRecurrenceType())
                .description(request.getDescription())
                .icon(request.getIcon())
                .targetCount(request.getTargetCount())
                .dailyMaxCount(request.getDailyMaxCount())
                .workType(request.getWorkType())
                .point(request.getPoint())
                .isActive(request.isActive())
                .build();

        // 사용자 저장
        Challenges savedChallenges = challengesRepository.save(rewards);
        return ChallengesResponse.from(savedChallenges);
    }

    // 수정
    @Transactional
    public ChallengesResponse updateChallenges(ChallengesUpdateRequest request, Long id) {
        Challenges getChallenges = challengesRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("도전과제를 찾을 수 없습니다: " + id));

        if(request.getName() != null
                && !request.getName().isBlank()
                && challengesRepository.existsByNameAndIsActiveTrueAndIdNot(request.getName(), id)) {
            // 중복 보상명 확인
            throw new CommonAlreadyExistsException("이미 사용중인 보상명 입니다: " + request.getName());
        }

        getChallenges.update(request);

        return ChallengesResponse.from(getChallenges);
    }

    // 삭제
    @Transactional
    public ChallengesResponse deleteChallenges(Long id) {
        Challenges getChallenges = challengesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("도전과제를 찾을 수 없습니다: " + id));

        if(!getChallenges.isActive()){
            return ChallengesResponse.from(getChallenges);
        }
        getChallenges.setActive(false);

        return ChallengesResponse.from(getChallenges);

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
