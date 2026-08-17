package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * AI 对话消息实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiConversationMessage {
    private Long id;                    // 消息 ID
    private Long sessionId;             // 会话 ID
    private Long userId;                // 用户 ID
    private String role;                // 角色：user/assistant
    private String message;             // 消息内容
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
