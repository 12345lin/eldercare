package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约时段详情 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotVO {
    private Long id;                          // 时段 ID
    private Long packageId;                   // 套餐 ID
    private LocalDate appointDate;            // 预约日期
    private String timeRange;                 // 时间段，如 "09:00-10:00"
    private Integer maxCount;                 // 最大预约人数
    private Integer currentCount;             // 当前已预约人数
    private String status;                    // 状态：AVAILABLE/FULL/CLOSED
    private LocalDateTime createTime;         // 创建时间
}
