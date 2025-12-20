package com.librarian.todo_list.challenges.controller;

import com.librarian.todo_list.challenges.dto.*;
import com.librarian.todo_list.challenges.service.ChallengesService;
import com.librarian.todo_list.challenges.service.UserProgressChallengesService;
import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user/challenges")
@RequiredArgsConstructor
public class UserProgressChallengesController {
    private final UserProgressChallengesService userProgressChallengesService;

    @PostMapping("/progress")
    public ResponseEntity<ApiResponse<UserProgressChallengesRes>> progressUserChallenge(
            @Valid @RequestBody UserProgressChallengesReq request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("도전과제 등록 API 호출: name={}", request.getName());

        UserProgressChallengesRes challengesResponse = userProgressChallengesService.progressChallenges(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(challengesResponse, "도전과제가 성공적으로 등록 완료되었습니다."));
    }
}
