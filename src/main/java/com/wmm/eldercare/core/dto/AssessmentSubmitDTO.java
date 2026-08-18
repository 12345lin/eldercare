package com.wmm.eldercare.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 提交答卷 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSubmitDTO {
    private Long questionnaireId;              // 问卷 ID
    private List<AnswerItemDTO> answers;       // 答案列表

    /**
     * 单个题目答案
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerItemDTO {
        private Long questionId;               // 题目 ID
        private String answer;                 // 用户答案（单选/多选选项值，文本题直接存文字）
    }
}
