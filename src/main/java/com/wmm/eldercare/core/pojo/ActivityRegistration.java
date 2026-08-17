package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 活动报名实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRegistration {
    private Long id;                    // 报名 ID
    private Long userId;                // 用户 ID
    private Long activityId;            // 活动 ID
    private String checkInStatus;       // 签到状态：NOT_CHECKED_IN/CHECKED_IN
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
