package com.librarian.todo_list.challenges.dto;

import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.points.entity.UserPoint;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressChallengesRes {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String description;
    private String icon;
    private UserPoint.PeriodTypeStatus recurrenceType;
    private Integer targetCount;
    private Integer point;
    private Boolean isActive;
    private Integer currentCount;
    private Boolean isAchieved;
    private UserPoint.PeriodTypeStatus periodType;
    private String periodKey;

    public static UserProgressChallengesRes from(UserProgressChallenges progress) {
        return UserProgressChallengesRes.builder()
                .id(progress.getId())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .name(progress.getChallenges().getName())
                .description(progress.getChallenges().getDescription())
                .icon(progress.getChallenges().getIcon())
                .recurrenceType(progress.getChallenges().getRecurrenceType())
                .targetCount(progress.getChallenges().getTargetCount())
                .point(progress.getChallenges().getPoint())
                .isActive(progress.getChallenges().isActive())
                .currentCount(progress.getCurrentCount())
                .isAchieved(progress.getIsAchieved())
                .periodType(progress.getPeriodType())
                .periodKey(progress.getPeriodKey())
                .build();
    }
}
