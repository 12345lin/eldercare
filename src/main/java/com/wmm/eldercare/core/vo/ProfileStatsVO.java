package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人中心统计面板 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatsVO {
    private Integer points;             // 当前积分
    private Integer healthRecordCount;  // 健康记录数
    private Integer assessmentCount;    // 健康评测次数
    private Integer appointmentCount;   // 体检预约数
    private Integer activityCount;      // 报名活动数
    private Integer unreadMessageCount; // 未读消息数
}