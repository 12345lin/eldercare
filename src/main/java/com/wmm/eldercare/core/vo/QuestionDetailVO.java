package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 题目详情 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailVO {
    private Long id;                           // 题目 ID
    private String content;                    // 题目内容
    private String type;                       // 类型：SINGLE/MULTIPLE/TEXT
    private String options;                    // 选项 JSON 数组
    private Integer sortOrder;                 // 排序号
}
