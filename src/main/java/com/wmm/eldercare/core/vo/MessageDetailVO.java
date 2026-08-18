package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息详情 VO（详情页展示，含完整内容）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDetailVO {
    private Long id;                 // 消息 ID
    private String title;            // 消息标题
    private String content;          // 消息完整内容
    private String type;             // 消息类型：APPOINTMENT/ACTIVITY/SYSTEM
    private Integer isRead;          // 是否已读：0 未读/1 已读
    private String createTime;       // 发送时间
}