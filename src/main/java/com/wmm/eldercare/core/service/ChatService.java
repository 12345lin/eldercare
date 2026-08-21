package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.pojo.AiConversationMessage;
import com.wmm.eldercare.core.pojo.AiConversationSession;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {
    /**
     * 创建会话
     * @param userId
     * @return
     */
    Long createSession(Long userId, String sessionName);

    List<AiConversationSession> findBySessionsByUserId(Long userId);

    int deleteById(Long id, Long sessionId);

    List<AiConversationMessage> getMessages(Long userId,Long sessionId);

    String sendMessage(Long userId,Long sessionId,String message);

    /**
     * 流式发送消息（SSE，打字机效果）
     * @param userId
     * @param sessionId
     * @param message
     * @param emitter SSE 发射器
     */
    void sendMessageStream(Long userId, Long sessionId, String message, SseEmitter emitter);
}
