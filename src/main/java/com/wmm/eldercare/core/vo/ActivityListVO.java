package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动列表 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListVO {
    private Long id;                          // 活动 ID
    private String title;                     // 活动标题
    private String coverUrl;                  // 封面图 URL
    private LocalDateTime activityStart;      // 活动开始时间
    private LocalDateTime activityEnd;        // 活动结束时间
    private Integer maxParticipants;          // 人数上限
    private Integer currentParticipants;      // 当前报名人数
    private String status;                    // 状态：REGISTRATING/IN_PROGRESS
    private Boolean registered;               // 当前用户是否已报名
}