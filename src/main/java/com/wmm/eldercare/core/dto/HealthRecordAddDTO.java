package com.wmm.eldercare.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 健康记录录入 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordAddDTO {
    private Integer systolic;      // 收缩压（mmHg）
    private Integer diastolic;     // 舒张压（mmHg）
    private BigDecimal bloodSugar; // 血糖（mmol/L）
    private Integer heartRate;     // 心率（次/分）
    private BigDecimal weight;     // 体重（kg）
    private String memo;           // 备注
}
