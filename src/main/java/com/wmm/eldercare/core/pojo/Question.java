package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 题目实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;                    // 题目 ID
    private Long questionnaireId;       // 问卷 ID
    private String content;             // 题目内容
    private String type;                // 类型：SINGLE 单选/MULTIPLE 多选/TEXT 文本
    private String scoreMode;           // 计分模式：SCORED 计分/NON_SCORED 非计分
    private Integer maxScore;           // 题目满分（计分题：最高选项分值；非计分题/文本题为0或AI评估上限）
    private String options;             // 选项 JSON 数组（计分题项含 score；非计分题项含 meaning；文本题为 NULL）
    private Integer sortOrder;          // 排序号
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
