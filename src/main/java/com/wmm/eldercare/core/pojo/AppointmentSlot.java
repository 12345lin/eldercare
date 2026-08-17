package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约时段实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlot {
    private Long id;                    // 时间段 ID
    private Long packageId;             // 套餐 ID
    private LocalDate appointDate;      // 预约日期
    private String timeRange;           // 时间段，如 "09:00-10:00"
    private Integer maxCount;           // 最大预约人数
    private Integer currentCount;       // 当前已预约人数
    private String status;              // 状态：AVAILABLE/FULL/CLOSED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
