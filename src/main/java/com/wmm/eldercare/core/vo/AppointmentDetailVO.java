package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预约记录详情 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailVO {
    private Long id;                          // 预约 ID
    private Long slotId;                      // 时段 ID
    private Long packageId;                   // 套餐 ID
    private String packageName;               // 套餐名称
    private String timeRange;                 // 时间段
    private String appointDate;               // 预约日期
    private String status;                    // 状态：PENDING/CONFIRMED/CANCELED/COMPLETED
    private String reportUrl;                 // 体检报告 URL
    private LocalDateTime createTime;         // 预约时间
}
