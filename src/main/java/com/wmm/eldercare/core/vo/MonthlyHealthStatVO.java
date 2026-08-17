package com.wmm.eldercare.core.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 每月健康指标统计（健康趋势查询的中间结果）
 * 由 SQL 按月份 GROUP BY 聚合而来
 */
@Data
public class MonthlyHealthStatVO {
    /** 月份，格式 yyyy-MM，如 2026-03 */
    private String month;

    /** 收缩压 */
    private BigDecimal avgSystolic;
    private BigDecimal maxSystolic;
    private BigDecimal minSystolic;

    /** 舒张压 */
    private BigDecimal avgDiastolic;
    private BigDecimal maxDiastolic;
    private BigDecimal minDiastolic;

    /** 血糖 */
    private BigDecimal avgBloodSugar;
    private BigDecimal maxBloodSugar;
    private BigDecimal minBloodSugar;

    /** 心率 */
    private BigDecimal avgHeartRate;
    private BigDecimal maxHeartRate;
    private BigDecimal minHeartRate;

    /** BMI */
    private BigDecimal avgBmi;
    private BigDecimal maxBmi;
    private BigDecimal minBmi;
}