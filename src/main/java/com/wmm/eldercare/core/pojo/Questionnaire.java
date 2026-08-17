package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 问卷实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {
    private Long id;                    // 问卷 ID
    private String title;               // 问卷标题
    private String description;         // 问卷描述
    private String status;              // 状态：DRAFT 草稿/PUBLISHED 已发布
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
