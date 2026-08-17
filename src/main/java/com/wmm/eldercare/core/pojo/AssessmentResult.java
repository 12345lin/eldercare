package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 评测结果实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {
    private Long id;                    // 评测结果 ID
    private Long userId;                // 用户 ID
    private Long questionnaireId;       // 问卷 ID
    private String answers;             // 答案快照 JSON
    private Integer aiScore;            // AI 评分（百分制）
    private String aiSuggestion;        // AI 建议
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
