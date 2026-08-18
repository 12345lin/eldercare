package com.wmm.eldercare.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端发送消息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendDTO {
    private Long userId;        // 接收用户 ID
    private String title;       // 消息标题
    private String content;     // 消息内容
    private String type;        // 消息类型：APPOINTMENT/ACTIVITY/SYSTEM
}