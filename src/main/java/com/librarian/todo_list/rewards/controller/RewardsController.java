package com.librarian.todo_list.rewards.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.rewards.dto.RewardsRegistrationRequest;
import com.librarian.todo_list.rewards.dto.RewardsResponse;
import com.librarian.todo_list.rewards.service.RewardsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardsController {
    private final RewardsService rewardsService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<RewardsResponse>>> getRewardList() {
        log.info("상품 목록 API 호출");

        List<RewardsResponse> rewardResponse = rewardsService.getRewards();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상 목록을 성공적으로 불러왔습니다."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardsResponse>> getOneReward(@PathVariable Long id) {
        log.info("특정 상품 API 호출: id={}", id);

        RewardsResponse rewardResponse = rewardsService.getOneReward(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상을 성공적으로 불러왔습니다."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RewardsResponse>> registerReward(
            @Valid @RequestBody RewardsRegistrationRequest request) {
        log.info("상품 등록 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.registerRewards(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 등록 완료되었습니다."));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse> updateReward(
//            @Valid @RequestBody RewardsRegistrationRequest request,
//            @PathVariable Long id ) {
//        log.info("상품 수정 API 호출: name={}", request.getName());
//
//        RewardsResponse rewardResponse = rewardsService.updateRewards(request, id);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 수정 완료되었습니다."));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardsResponse>> deleteReward(@PathVariable Long id) {
        log.info("상품 삭제 API 호출: id={}", id);

        RewardsResponse rewardResponse = rewardsService.deleteRewards(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 삭제 완료되었습니다."));
    }
}
