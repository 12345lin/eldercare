package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 问卷详情 VO（含题目列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDetailVO {
    private Long id;                           // 问卷 ID
    private String title;                      // 问卷标题
    private String description;                // 问卷描述
    private List<QuestionDetailVO> questions;  // 题目列表
}
