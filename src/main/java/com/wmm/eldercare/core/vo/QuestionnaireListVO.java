package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 问卷列表 VO（分页返回）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireListVO {
    private Long id;                           // 问卷 ID
    private String title;                      // 问卷标题
    private String description;                // 问卷描述
    private String createTime;                 // 发布时间
}
