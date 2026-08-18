package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评测结果 VO（我的评测历史）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultVO {
    private Long id;                           // 评测结果 ID
    private Long questionnaireId;              // 问卷 ID
    private String questionnaireTitle;         // 问卷标题
    private Integer aiScore;                   // AI 评分（无AI版本为null）
    private String aiSuggestion;               // AI 建议（无AI版本为null）
    private String createTime;                 // 评测时间
}
