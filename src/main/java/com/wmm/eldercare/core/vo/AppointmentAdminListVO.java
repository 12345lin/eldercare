package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端预约列表 VO（含用户手机号、套餐名、时段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentAdminListVO {
    private Long id;                 // 预约 ID
    private Long userId;             // 用户 ID
    private String phone;            // 手机号
    private String realName;         // 真实姓名
    private String packageName;      // 套餐名
    private String appointDate;      // 预约日期
    private String timeRange;        // 时段
    private String status;           // 状态：PENDING/CONFIRMED/CANCELED/COMPLETED
    private String reportUrl;        // 体检报告 URL
    private String createTime;       // 预约时间
}