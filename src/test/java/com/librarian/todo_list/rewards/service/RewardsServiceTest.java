package com.librarian.todo_list.rewards.service;

import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.rewards.dto.RewardsRegistrationRequest;
import com.librarian.todo_list.rewards.dto.RewardsResponse;
import com.librarian.todo_list.rewards.dto.RewardsUpdateRequest;
import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.rewards.repository.RewardsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("RewardsService 단위 테스트")
class RewardsServiceTest {

    @Mock
    private RewardsRepository rewardsRepository;

    @InjectMocks
    private RewardsService rewardsService;

    private Rewards activeReward;

    @BeforeEach
    void setUp() {
        activeReward = Rewards.builder()
                .name("테스트 보상")
                .type(Rewards.RewardsType.POINT)
                .point(100)
                .description("테스트 보상 설명")
                .discount(false)
                .discountRate(0)
                .isActive(true)
                .build();
    }

    // -----------------------------------------------------------------------
    // getRewards
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("활성화된 보상 목록 조회 성공")
    void getRewards_returnsActiveRewardsList() {
        given(rewardsRepository.findByIsActiveTrue()).willReturn(List.of(activeReward));

        List<RewardsResponse> result = rewardsService.getRewards();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 보상");
    }

    @Test
    @DisplayName("활성화된 보상이 없으면 빈 목록 반환")
    void getRewards_withNoActiveRewards_returnsEmptyList() {
        given(rewardsRepository.findByIsActiveTrue()).willReturn(List.of());

        List<RewardsResponse> result = rewardsService.getRewards();

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // getOneReward
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하는 ID의 활성 보상 단건 조회 성공")
    void getOneReward_withExistingActiveId_returnsRewardResponse() {
        given(rewardsRepository.findById(1L)).willReturn(Optional.of(activeReward));

        RewardsResponse result = rewardsService.getOneReward(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 보상");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 IllegalArgumentException 발생")
    void getOneReward_withNonExistingId_throwsIllegalArgumentException() {
        given(rewardsRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> rewardsService.getOneReward(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("비활성화된 보상 조회 시 IllegalArgumentException 발생")
    void getOneReward_withInactiveReward_throwsIllegalArgumentException() {
        Rewards inactiveReward = Rewards.builder()
                .name("비활성 보상")
                .type(Rewards.RewardsType.POINT)
                .point(50)
                .description("삭제된 보상")
                .isActive(false)
                .build();

        given(rewardsRepository.findById(2L)).willReturn(Optional.of(inactiveReward));

        assertThatThrownBy(() -> rewardsService.getOneReward(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제된 보상");
    }

    // -----------------------------------------------------------------------
    // registerRewards
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 요청으로 보상 등록 성공")
    void registerRewards_withValidRequest_returnsRewardResponse() {
        RewardsRegistrationRequest request = RewardsRegistrationRequest.builder()
                .name("신규 보상")
                .type(Rewards.RewardsType.POINT)
                .point(200)
                .description("신규 보상 설명")
                .discount(false)
                .discountRate(0)
                .isActive(true)
                .build();

        given(rewardsRepository.existsByName("신규 보상")).willReturn(false);
        given(rewardsRepository.save(any(Rewards.class))).willReturn(activeReward);

        RewardsResponse result = rewardsService.registerRewards(request);

        assertThat(result).isNotNull();
        then(rewardsRepository).should().save(any(Rewards.class));
    }

    @Test
    @DisplayName("중복 이름으로 보상 등록 시 CommonAlreadyExistsException 발생")
    void registerRewards_withDuplicateName_throwsException() {
        RewardsRegistrationRequest request = RewardsRegistrationRequest.builder()
                .name("테스트 보상")
                .type(Rewards.RewardsType.POINT)
                .point(100)
                .description("설명")
                .build();

        given(rewardsRepository.existsByName("테스트 보상")).willReturn(true);

        assertThatThrownBy(() -> rewardsService.registerRewards(request))
                .isInstanceOf(CommonAlreadyExistsException.class);

        then(rewardsRepository).should(never()).save(any());
    }

    // -----------------------------------------------------------------------
    // updateRewards
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 요청으로 보상 수정 성공")
    void updateRewards_withValidRequest_returnsUpdatedReward() {
        RewardsUpdateRequest request = new RewardsUpdateRequest(
                "수정된 보상", Rewards.RewardsType.COUPON, 150, "수정된 설명", false, 0);

        given(rewardsRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(activeReward));
        given(rewardsRepository.existsByNameAndIsActiveTrueAndIdNot("수정된 보상", 1L)).willReturn(false);

        RewardsResponse result = rewardsService.updateRewards(request, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 보상 수정 시 IllegalArgumentException 발생")
    void updateRewards_withNonExistingId_throwsIllegalArgumentException() {
        RewardsUpdateRequest request = new RewardsUpdateRequest(
                "수정된 보상", null, null, null, null, null);

        given(rewardsRepository.findByIdAndIsActiveTrue(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> rewardsService.updateRewards(request, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("다른 보상과 중복되는 이름으로 수정 시 CommonAlreadyExistsException 발생")
    void updateRewards_withDuplicateNameForOtherReward_throwsException() {
        RewardsUpdateRequest request = new RewardsUpdateRequest(
                "다른 보상 이름", null, null, null, null, null);

        given(rewardsRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(activeReward));
        given(rewardsRepository.existsByNameAndIsActiveTrueAndIdNot("다른 보상 이름", 1L)).willReturn(true);

        assertThatThrownBy(() -> rewardsService.updateRewards(request, 1L))
                .isInstanceOf(CommonAlreadyExistsException.class);
    }

    // -----------------------------------------------------------------------
    // deleteRewards
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하는 활성 보상 삭제 성공 (소프트 삭제)")
    void deleteRewards_withExistingActiveReward_deactivatesReward() {
        given(rewardsRepository.findById(1L)).willReturn(Optional.of(activeReward));

        RewardsResponse result = rewardsService.deleteRewards(1L);

        assertThat(result).isNotNull();
        assertThat(activeReward.isActive()).isFalse();
    }

    @Test
    @DisplayName("이미 비활성화된 보상 삭제 요청 시 그대로 반환")
    void deleteRewards_withAlreadyInactiveReward_returnsWithoutChange() {
        Rewards inactiveReward = Rewards.builder()
                .name("이미 삭제된 보상")
                .type(Rewards.RewardsType.POINT)
                .point(50)
                .description("설명")
                .isActive(false)
                .build();

        given(rewardsRepository.findById(2L)).willReturn(Optional.of(inactiveReward));

        RewardsResponse result = rewardsService.deleteRewards(2L);

        assertThat(result).isNotNull();
        assertThat(inactiveReward.isActive()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 보상 삭제 시 IllegalArgumentException 발생")
    void deleteRewards_withNonExistingId_throwsIllegalArgumentException() {
        given(rewardsRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> rewardsService.deleteRewards(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }
}
