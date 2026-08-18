package com.wmm.eldercare.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmm.eldercare.core.mapper.AiConversationMessageMapper;
import com.wmm.eldercare.core.mapper.AiConversationSessionMapper;
import com.wmm.eldercare.core.mapper.SysConfigMapper;
import com.wmm.eldercare.core.pojo.AiConversationMessage;
import com.wmm.eldercare.core.pojo.AiConversationSession;
import com.wmm.eldercare.core.pojo.SysConfig;
import com.wmm.eldercare.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final AiConversationSessionMapper sessionMapper;
    private final AiConversationMessageMapper messageMapper;
    private final SysConfigMapper sysConfigMapper;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long createSession(Long userId, String sessionName) {
        AiConversationSession session = new AiConversationSession();
        session.setUserId(userId);
        session.setSessionName(sessionName != null ? sessionName : "新对话");
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        session.setDeleted(0);
        sessionMapper.insert(session);
        return session.getId();
    }

    @Override
    public List<AiConversationSession> findBySessionsByUserId(Long userId) {
        return sessionMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public int deleteById(Long userId, Long sessionId) {
        // 1. 校验会话属于该用户
        AiConversationSession session = sessionMapper.findById(sessionId);
        if (session == null || !userId.equals(session.getUserId())) {
            throw new RuntimeException("会话不存在或无权删除");
        }
        // 2. 级联删除消息
        messageMapper.deleteBySessionId(sessionId);
        // 3. 删除会话
        return sessionMapper.deleteById(sessionId, userId);
    }

    @Override
    public List<AiConversationMessage> getMessages(Long userId, Long sessionId) {
        // 校验会话归属
        AiConversationSession session = sessionMapper.findById(sessionId);
        if (session == null || !userId.equals(session.getUserId())) {
            throw new RuntimeException("会话不存在或无权查看");
        }
        return messageMapper.findBySessionId(sessionId);
    }

    @Override
    @Transactional
    public String sendMessage(Long userId, Long sessionId, String message) {
        // 1. 校验会话
        AiConversationSession session = sessionMapper.findById(sessionId);
        if (session == null || !userId.equals(session.getUserId())) {
            throw new RuntimeException("会话不存在或无权发送");
        }

        // 2. 保存用户消息
        AiConversationMessage userMsg = new AiConversationMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setUserId(userId);
        userMsg.setRole("user");
        userMsg.setMessage(message);
        userMsg.setCreateTime(LocalDateTime.now());
        userMsg.setUpdateTime(LocalDateTime.now());
        userMsg.setDeleted(0);
        messageMapper.insert(userMsg);

        // 3. 查询最近 10 轮上下文（20 条消息）
        List<AiConversationMessage> allMessages = messageMapper.findBySessionId(sessionId);
        List<AiConversationMessage> context = allMessages.subList(
            Math.max(0, allMessages.size() - 20),
            allMessages.size()
        );

        // 4. 获取系统提示词
        String systemPrompt = getSystemPrompt();

        // 5. 构建对话历史
        StringBuilder history = new StringBuilder();
        for (AiConversationMessage msg : context) {
            String role = "user".equals(msg.getRole()) ? "用户" : "AI";
            history.append(role).append(": ").append(msg.getMessage()).append("\n");
        }

        // 6. 调用 AI
        String aiResponse = chatClient.prompt()
            .system(systemPrompt)
            .user(history.toString() + "\n用户: " + message)
            .call()
            .content();

        // 7. 保存 AI 回复
        AiConversationMessage assistantMsg = new AiConversationMessage();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setUserId(userId);
        assistantMsg.setRole("assistant");
        assistantMsg.setMessage(aiResponse);
        assistantMsg.setCreateTime(LocalDateTime.now());
        assistantMsg.setUpdateTime(LocalDateTime.now());
        assistantMsg.setDeleted(0);
        messageMapper.insert(assistantMsg);

        return aiResponse;
    }

    /**
     * 从 SysConfig 获取系统提示词
     */
    private String getSystemPrompt() {
        SysConfig config = sysConfigMapper.findByKey("ai_chat_system_prompt");
        return config != null ? config.getConfigValue() : "你是一位专业的健康顾问。";
    }
}
