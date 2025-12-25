package com.librarian.todo_list.rewards.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.rewards.dto.RewardsResgistrationRequest;
import com.librarian.todo_list.rewards.dto.RewardsResponse;
import com.librarian.todo_list.rewards.service.RewardsService;
import com.librarian.todo_list.user.dto.UserRegistrationRequest;
import com.librarian.todo_list.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardsController {
    private final RewardsService rewardsService;

    @GetMapping("/register")
    public ResponseEntity<ApiResponse<RewardsResponse>> getRewardList(
            @Valid @RequestBody RewardsResgistrationRequest request) {
        log.info("상품등록 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.registerRewards(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 등록 완료되었습니다."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RewardsResponse>> registerReward(
            @Valid @RequestBody RewardsResgistrationRequest request) {
        log.info("상품등록 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.registerRewards(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 등록 완료되었습니다."));
    }

    @PutMapping("/register")
    public ResponseEntity<ApiResponse<RewardsResponse>> updateReward(
            @Valid @RequestBody RewardsResgistrationRequest request) {
        log.info("상품 수정 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.registerRewards(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 수정 완료되었습니다."));
    }

    @DeleteMapping("/register")
    public ResponseEntity<ApiResponse<RewardsResponse>> deleteReward(
            @Valid @RequestBody RewardsResgistrationRequest request) {
        log.info("상품등록 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.registerRewards(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 삭제 완료되었습니다."));
    }
}
