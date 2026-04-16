package com.librarian.todo_list.challenges.service;

import com.librarian.todo_list.challenges.dto.*;
import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.challenges.repository.ChallengesRepository;
import com.librarian.todo_list.challenges.repository.UserProgressChallengesRepository;
import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProgressChallengesService {

    private final UserProgressChallengesRepository userProgressChallengesRepository;
    private final ChallengesRepository challengesRepository;



    public List<ChallengesWithProgressResponse> getChallengesWithProgress(User user) {
        LocalDate now = LocalDate.now();

        Map<Long, UserProgressChallenges> progressMap = userProgressChallengesRepository
                .findCurrentPeriodProgressByUser(user,
                        generatePeriodKey(UserPoint.PeriodTypeStatus.DAILY,now),
                        generatePeriodKey(UserPoint.PeriodTypeStatus.WEEKLY,now),
                        generatePeriodKey(UserPoint.PeriodTypeStatus.MONTHLY,now))
                .stream()
                .collect(Collectors.toMap(p -> p.getChallenges().getId(), p -> p));

        return challengesRepository.findByIsActiveTrue()
                .stream()
                .map(challenge -> ChallengesWithProgressResponse.from(challenge, progressMap.get(challenge.getId())))
                .toList();
    }

    public List<UserProgressChallengesRes> getAchievedChallenges(User user) {
        return userProgressChallengesRepository.findByUserAndIsAchievedTrue(user)
                .stream()
                .map(UserProgressChallengesRes::from)
                .toList();
    }

    public List<ChallengesWithProgressResponse> getMatchChallenges(User user) {
        LocalDate now = LocalDate.now();
        return userProgressChallengesRepository.findUserMatchChallengeByUser(user,
                        generatePeriodKey(UserPoint.PeriodTypeStatus.DAILY,now),
                        generatePeriodKey(UserPoint.PeriodTypeStatus.WEEKLY,now),
                        generatePeriodKey(UserPoint.PeriodTypeStatus.MONTHLY,now))
                .stream()
                .toList();
    }

    @Transactional
    public UserProgressChallengesRes progressChallenges(UserProgressChallengesReq request) {

        // create Rewards Entity
        UserProgressChallenges rewards = UserProgressChallenges.builder()
                .build();

        // 사용자 저장
        UserProgressChallenges savedChallenges = userProgressChallengesRepository.save(rewards);
        return UserProgressChallengesRes.from(savedChallenges);

    }

    private String generatePeriodKey(UserPoint.PeriodTypeStatus periodType, LocalDate date) {
        return switch (periodType) {
            case DAILY -> date.toString();
            case WEEKLY -> date.with(DayOfWeek.MONDAY).toString();
            case MONTHLY -> date.withDayOfMonth(1).toString();
        };
    }
}
