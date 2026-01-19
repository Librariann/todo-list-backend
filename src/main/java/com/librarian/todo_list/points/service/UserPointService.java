package com.librarian.todo_list.points.service;

import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.points.repository.UserPointRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointService {

    private final UserPointRepository userPointRepository;

    /**
     * 챌린지 완료로 인한 포인트 지급
     */
    @Transactional
    public void awardChallengePoints(User user, Integer points, Long challengeId, 
                                   UserPoint.PeriodTypeStatus periodType) {
        log.info("챌린지 포인트 지급 - 사용자: {}, 포인트: {}, 챌린지ID: {}", 
                user.getId(), points, challengeId);

        LocalDate now = LocalDate.now();
        String periodKey = generatePeriodKey(periodType, now);

        UserPoint userPoint = UserPoint.builder()
                .user(user)
                .action(UserPoint.ActionStatus.CREDIT)
                .reason(UserPoint.ReasonStatus.CHALLENGE)
                .metaType(UserPoint.MetaTypeStatus.CHALLENGE)
                .metaId(challengeId)
                .periodType(periodType)
                .periodKey(periodKey)
                .point(points)
                .build();

        userPointRepository.save(userPoint);
        log.info("포인트 지급 완료 - 사용자: {}, 포인트: {}", user.getId(), points);
    }

    /**
     * 기간별 키 생성
     */
    private String generatePeriodKey(UserPoint.PeriodTypeStatus periodType, LocalDate date) {
        return switch (periodType) {
            case DAILY -> date.toString(); // "2026-01-25"
            case WEEKLY -> date.with(DayOfWeek.MONDAY).toString(); // 주 시작일 (월요일)
            case MONTHLY -> date.withDayOfMonth(1).toString(); // 월 시작일
        };
    }

    /**
     * 사용자의 총 포인트 조회 (적립 - 사용)
     */
    public Integer getUserTotalPoints(User user) {
        return userPointRepository.calculateUserTotalPoints(user.getId()).orElse(0);
    }

    /**
     * 특정 기간의 포인트 내역 조회
     */
    public Integer getUserPointsByPeriod(User user, UserPoint.PeriodTypeStatus periodType, 
                                       String periodKey) {
        return userPointRepository.findPointsByUserAndPeriod(
            user.getId(), periodType, periodKey).orElse(0);
    }
}