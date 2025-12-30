package com.librarian.todo_list.challenges.service;

import com.librarian.todo_list.challenges.dto.*;
import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.challenges.repository.ChallengesRepository;
import com.librarian.todo_list.challenges.repository.UserProgressChallengesRepository;
import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProgressChallengesService {

    private final UserProgressChallengesRepository userProgressChallengesRepository;

    @Transactional
    public UserProgressChallengesRes progressChallenges(UserProgressChallengesReq request) {

        // create Rewards Entity
        UserProgressChallenges rewards = UserProgressChallenges.builder()
                .build();

        // 사용자 저장
        UserProgressChallenges savedChallenges = userProgressChallengesRepository.save(rewards);
        return UserProgressChallengesRes.from(savedChallenges);

    }
}
