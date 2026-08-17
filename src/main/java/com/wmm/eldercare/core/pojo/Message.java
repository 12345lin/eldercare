package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 站内消息实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;                    // 消息 ID
    private Long userId;                // 用户 ID
    private String title;               // 消息标题
    private String content;             // 消息内容
    private String type;                // 消息类型：APPOINTMENT/ACTIVITY/SYSTEM
    private Integer isRead;             // 是否已读：0 未读/1 已读
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
