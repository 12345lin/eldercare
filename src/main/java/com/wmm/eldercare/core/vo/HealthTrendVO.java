package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 健康趋势 VO（GET /api/member/health/trend 返回值）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthTrendVO {
    private List<String> months;              // 月份列表 ["2026-03", "2026-04", ...]
    private IndicatorTrend systolic;          // 收缩压趋势
    private IndicatorTrend diastolic;         // 舒张压趋势
    private IndicatorTrend bloodSugar;        // 血糖趋势
    private IndicatorTrend heartRate;         // 心率趋势
    private IndicatorTrend bmi;               // BMI 趋势
}
