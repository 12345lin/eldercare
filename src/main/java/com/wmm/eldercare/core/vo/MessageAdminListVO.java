package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端消息列表 VO（含接收用户手机号）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAdminListVO {
    private Long id;                 // 消息 ID
    private Long userId;             // 接收用户 ID
    private String phone;            // 接收用户手机号
    private String title;            // 消息标题
    private String type;             // 消息类型：APPOINTMENT/ACTIVITY/SYSTEM
    private Integer isRead;          // 是否已读：0 未读/1 已读
    private String createTime;       // 发送时间
}