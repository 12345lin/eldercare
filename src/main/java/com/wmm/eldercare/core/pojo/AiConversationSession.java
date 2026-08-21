package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * AI 对话会话实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiConversationSession {
    private Long id;                    // 会话 ID
    private Long userId;                // 用户 ID
    private String sessionName;         // 会话名称
    private String firstMessage;        // 首条用户消息（用于列表预览）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
