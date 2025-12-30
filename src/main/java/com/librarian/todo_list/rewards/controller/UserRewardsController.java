package com.librarian.todo_list.rewards.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.rewards.dto.*;
import com.librarian.todo_list.rewards.service.RewardsService;
import com.librarian.todo_list.rewards.service.UserRewardsService;
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
@RequestMapping("/api/user/rewards")
@RequiredArgsConstructor
public class UserRewardsController {
    private final UserRewardsService userRewardsService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<UserRewardsResponse>>> getUserRewardList() {
        log.info("유저 상품 목록 API 호출");

        List<UserRewardsResponse> rewardResponse = userRewardsService.getUserRewards();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "받은 보상 목록을 성공적으로 불러왔습니다."));
    }

    @PostMapping("/{rewardId}/redeem")
    public ResponseEntity<ApiResponse<UserRewardsResponse>> redeemUserReward(
            @PathVariable Long rewardId,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("유저 상품 등록 API 호출: rewardId={}", rewardId);

        UserRewardsResponse rewardResponse = userRewardsService.redeemUserRewards(rewardId, principal.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 지급되었습니다"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> useUserReward(@PathVariable Long id) {
        log.info("유저 상품 사용 API 호출: id={}", id);

        UserRewardsResponse rewardResponse = userRewardsService.useUserRewards(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 사용 완료되었습니다."));
    }
}
