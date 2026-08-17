package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康记录实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {
    private Long id;                    // 记录 ID
    private Long userId;                // 用户 ID
    private Integer systolic;           // 收缩压（mmHg）
    private Integer diastolic;          // 舒张压（mmHg）
    private BigDecimal bloodSugar;      // 血糖（mmol/L）
    private Integer heartRate;          // 心率（次/分）
    private BigDecimal weight;          // 体重（kg）
    private BigDecimal bmi;             // BMI 指数
    private String memo;                // 备注
    private LocalDateTime recordedAt;   // 记录时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
