package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端仪表盘统计 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {
    // 用户统计
    private Long totalUsers;           // 总用户数
    private Long todayNewUsers;        // 今日新增用户
    private Long enabledUsers;         // 启用中用户数
    private Long disabledUsers;        // 已禁用用户数

    // 预约统计
    private Long totalAppointments;    // 总预约数
    private Long todayAppointments;    // 今日预约数
    private Long pendingAppointments;  // 待处理预约数

    // 活动统计
    private Long totalActivities;      // 总活动数
    private Long activeActivities;     // 进行中活动数

    // 健康记录
    private Long totalHealthRecords;   // 总健康记录数

    // 消息统计
    private Long unreadMessages;       // 未读消息总数
}
