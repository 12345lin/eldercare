package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.pojo.AiConversationMessage;
import com.wmm.eldercare.core.pojo.AiConversationSession;

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
}
