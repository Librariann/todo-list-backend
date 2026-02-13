package com.librarian.todo_list.challenges.service;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.challenges.repository.ChallengesRepository;
import com.librarian.todo_list.challenges.repository.UserProgressChallengesRepository;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.points.service.UserPointService;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeProgressService {

    private final ChallengesRepository challengesRepository;
    private final UserProgressChallengesRepository userProgressChallengesRepository;
    private final UserPointService userPointService;

    @Transactional
    public void updateTodosChallengeProgress(User user, LocalDate date) {
        log.info("TODO 챌린지 진행도 업데이트 시작 - 사용자: {}, 날짜: {}", user.getId(), date);

        List<Challenges> activeTodosChallenges = challengesRepository
                .findByWorkTypeAndIsActiveTrue(Challenges.WorkType.TODOS);

        log.info("활성화된 TODOS 챌린지 개수: {}", activeTodosChallenges.size());

        for (Challenges challenge : activeTodosChallenges) {
            log.info("[Todos] 챌린지 처리 시작 - 챌린지ID: {}, 이름: {}", challenge.getId(), challenge.getName());
            updateChallengeProgress(user, challenge, date);
        }

        log.info("TODO 챌린지 진행도 업데이트 완료 - 사용자: {}", user.getId());
    }

    @Transactional
    public void updateGoalsChallengeProgress(User user, LocalDate date) {
        log.info("GOALS 챌린지 진행도 업데이트 시작 - 사용자: {}, 날짜: {}", user.getId(), date);

        List<Challenges> activeGoalsChallenges = challengesRepository
                .findByWorkTypeAndIsActiveTrue(Challenges.WorkType.GOALS);

        log.info("활성화된 GOALS 챌린지 개수: {}", activeGoalsChallenges.size());

        for (Challenges challenge : activeGoalsChallenges) {
            log.info("[Goals] 챌린지 처리 시작 - 챌린지ID: {}, 이름: {}", challenge.getId(), challenge.getName());
            updateChallengeProgress(user, challenge, date);
        }

        log.info("GOALS 챌린지 진행도 업데이트 완료 - 사용자: {}", user.getId());
    }

    @Transactional
    public void updateHabitsChallengeProgress(User user, LocalDate date) {
        log.info("HABITS 챌린지 진행도 업데이트 시작 - 사용자: {}, 날짜: {}", user.getId(), date);

        List<Challenges> activeTodosChallenges = challengesRepository
                .findByWorkTypeAndIsActiveTrue(Challenges.WorkType.HABITS);

        log.info("활성화된 HABITS 챌린지 개수: {}", activeTodosChallenges.size());

        for (Challenges challenge : activeTodosChallenges) {
            log.info("[Habits] 챌린지 처리 시작 - 챌린지ID: {}, 이름: {}", challenge.getId(), challenge.getName());
            updateChallengeProgress(user, challenge, date);
        }

        log.info("HABITS 챌린지 진행도 업데이트 완료 - 사용자: {}", user.getId());
    }

    private void updateChallengeProgress(User user, Challenges challenge, LocalDate date) {
        String periodKey = generatePeriodKey(challenge.getRecurrenceType(), date);
        log.info("기간키 생성: {}, 챌린지: {}", periodKey, challenge.getId());
        
        UserProgressChallenges progress = userProgressChallengesRepository
            .findByUserAndChallengesAndPeriodTypeAndPeriodKey(
                user, challenge, challenge.getRecurrenceType(), periodKey)
            .orElseGet(() -> {
                log.info("새로운 진행도 레코드 생성 - 사용자: {}, 챌린지: {}", user.getId(), challenge.getId());
                return createNewProgress(user, challenge, periodKey);
            });

        log.info("현재 진행도 조회 완료 - ID: {}, 현재카운트: {}, 달성여부: {}", 
            progress.getId(), progress.getCurrentCount(), progress.getIsAchieved());

        if (Boolean.TRUE.equals(progress.getIsAchieved())) {
            log.info("이미 달성된 챌린지 - 사용자: {}, 챌린지: {}", user.getId(), challenge.getId());
            return;
        }

        int newCurrentCount = progress.getCurrentCount() + 1;
        
        if (newCurrentCount > challenge.getDailyMaxCount()) {
            log.info("일일 최대 카운트 초과 - 사용자: {}, 챌린지: {}, 현재: {}, 최대: {}", 
                user.getId(), challenge.getId(), newCurrentCount, challenge.getDailyMaxCount());
            return;
        }

        progress.setCurrentCount(newCurrentCount);

        if (newCurrentCount >= challenge.getTargetCount()) {
            progress.setIsAchieved(true);
            userPointService.awardChallengePoints(
                user, challenge.getPoint(), challenge.getId(), challenge.getRecurrenceType());
            
            log.info("챌린지 달성! - 사용자: {}, 챌린지: {}, 포인트: {}", 
                user.getId(), challenge.getId(), challenge.getPoint());
        } else {
            log.info("챌린지 진행도 업데이트 - 사용자: {}, 챌린지: {}, 진행도: {}/{}", 
                user.getId(), challenge.getId(), newCurrentCount, challenge.getTargetCount());
        }

        log.info("진행도 저장 시작 - ID: {}, 새로운카운트: {}", progress.getId(), newCurrentCount);
        userProgressChallengesRepository.save(progress);
        log.info("진행도 저장 완료 - 사용자: {}, 챌린지: {}", user.getId(), challenge.getId());
    }

    private UserProgressChallenges createNewProgress(User user, Challenges challenge, String periodKey) {
        return UserProgressChallenges.builder()
            .user(user)
            .challenges(challenge)
            .periodType(challenge.getRecurrenceType())
            .periodKey(periodKey)
            .currentCount(0)
            .isAchieved(false)
            .build();
    }

    private String generatePeriodKey(UserPoint.PeriodTypeStatus periodType, LocalDate date) {
        return switch (periodType) {
            case DAILY -> date.toString();
            case WEEKLY -> date.with(DayOfWeek.MONDAY).toString();
            case MONTHLY -> date.withDayOfMonth(1).toString();
        };
    }
}