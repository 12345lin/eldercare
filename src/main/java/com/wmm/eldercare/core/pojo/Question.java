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
    private String options;             // 选项 JSON 数组
    private Integer sortOrder;          // 排序号
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
