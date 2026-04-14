package com.librarian.todo_list.challenges.dto;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.points.entity.UserPoint;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengesWithProgressResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String description;
    private String icon;
    private UserPoint.PeriodTypeStatus recurrenceType;
    private Challenges.WorkType workType;
    private Integer targetCount;
    private Integer point;
    private Integer currentCount;
    private Boolean isAchieved;
    private String periodKey;

    public static ChallengesWithProgressResponse from(Challenges challenge, UserProgressChallenges progress) {
        ChallengesWithProgressResponseBuilder builder = ChallengesWithProgressResponse.builder()
                .id(challenge.getId())
                .createdAt(challenge.getCreatedAt())
                .updatedAt(challenge.getUpdatedAt())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .icon(challenge.getIcon())
                .recurrenceType(challenge.getRecurrenceType())
                .workType(challenge.getWorkType())
                .targetCount(challenge.getTargetCount())
                .point(challenge.getPoint());

        if (progress != null) {
            builder.currentCount(progress.getCurrentCount())
                    .isAchieved(progress.getIsAchieved())
                    .periodKey(progress.getPeriodKey());
        } else {
            builder.currentCount(0).isAchieved(false);
        }

        return builder.build();
    }
}
