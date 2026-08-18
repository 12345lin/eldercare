package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 我的活动 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyActivityVO {
    private Long registrationId;              // 报名记录 ID
    private Long activityId;                  // 活动 ID
    private String title;                     // 活动标题
    private String coverUrl;                  // 封面图 URL
    private LocalDateTime activityStart;      // 活动开始时间
    private LocalDateTime activityEnd;        // 活动结束时间
    private String status;                    // 活动状态：REGISTRATING/IN_PROGRESS/ENDED
    private String checkInStatus;             // 签到状态：NOT_CHECKED_IN/CHECKED_IN
    private LocalDateTime createTime;         // 报名时间
}