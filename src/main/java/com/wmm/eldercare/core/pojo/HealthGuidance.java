package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 健康指导实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthGuidance {
    private Long id;                    // 指导 ID
    private Long userId;                // 用户 ID
    private String type;                // 类型：DIET/EXERCISE/DAILY/DATA_SUMMARY
    private String content;             // 指导内容
    private Integer isRead;             // 是否已读：0 未读/1 已读
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
