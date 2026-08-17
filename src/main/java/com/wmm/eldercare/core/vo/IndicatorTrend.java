package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 单项指标趋势数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorTrend {
    private List<BigDecimal> avg;  // 每月平均值
    private List<BigDecimal> max;  // 每月最大值
    private List<BigDecimal> min;  // 每月最小值
}
