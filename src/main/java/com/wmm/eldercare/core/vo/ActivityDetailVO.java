package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动详情 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetailVO {
    private Long id;                          // 活动 ID
    private String title;                     // 活动标题
    private String coverUrl;                  // 封面图 URL
    private String content;                   // 活动内容
    private LocalDateTime registrationStart;  // 报名开始时间
    private LocalDateTime registrationEnd;    // 报名结束时间
    private LocalDateTime activityStart;      // 活动开始时间
    private LocalDateTime activityEnd;        // 活动结束时间
    private Integer maxParticipants;          // 人数上限
    private Integer currentParticipants;      // 当前报名人数
    private String status;                    // 状态：DRAFT/REGISTRATING/IN_PROGRESS/ENDED
    private Boolean registered;               // 当前用户是否已报名
    private String checkInStatus;             // 签到状态：NOT_CHECKED_IN/CHECKED_IN(未报名为 null)
}