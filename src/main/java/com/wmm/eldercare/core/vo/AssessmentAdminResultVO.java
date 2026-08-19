package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端评测结果 VO（含用户手机号/姓名 + 问卷标题）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentAdminResultVO {
    private Long id;                 // 评测结果 ID
    private Long userId;             // 用户 ID
    private String phone;            // 手机号
    private String realName;         // 真实姓名
    private Long questionnaireId;    // 问卷 ID
    private String questionnaireTitle; // 问卷标题
    private Integer aiScore;         // AI 评分
    private String aiSuggestion;     // AI 建议
    private String createTime;       // 评测时间
}