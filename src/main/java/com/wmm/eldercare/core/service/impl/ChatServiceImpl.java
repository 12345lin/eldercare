package com.wmm.eldercare.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmm.eldercare.core.mapper.AiConversationMessageMapper;
import com.wmm.eldercare.core.mapper.AiConversationSessionMapper;
import com.wmm.eldercare.core.mapper.SysConfigMapper;
import com.wmm.eldercare.core.pojo.AiConversationMessage;
import com.wmm.eldercare.core.pojo.AiConversationSession;
import com.wmm.eldercare.core.pojo.SysConfig;
import com.wmm.eldercare.core.service.ChatService;
import com.wmm.eldercare.core.tool.AppointmentTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    private final AppointmentTools appointmentTools;
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

        // 6. 调用 AI（挂载预约工具，实现"AI帮用户预约"）
        String aiResponse;
        AppointmentTools.bindUser(userId);
        try {
            aiResponse = chatClient.prompt()
                .system(systemPrompt)
                .user(history.toString() + "\n用户: " + message)
                .tools(appointmentTools)   // 让 AI 能调用搜索套餐/查时段/下单预约
                .call()
                .content();
        } finally {
            AppointmentTools.clearUser();
        }

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

    @Override
    public void sendMessageStream(Long userId, Long sessionId, String message, SseEmitter emitter) {
        // 1. 校验会话
        AiConversationSession session = sessionMapper.findById(sessionId);
        if (session == null || !userId.equals(session.getUserId())) {
            try {
                emitter.send(SseEmitter.event().name("error").data("会话不存在或无权访问"));
            } catch (Exception ignored) {}
            emitter.complete();
            return;
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

        // 3. 最近上下文
        List<AiConversationMessage> allMessages = messageMapper.findBySessionId(sessionId);
        List<AiConversationMessage> context = allMessages.subList(
            Math.max(0, allMessages.size() - 20), allMessages.size());
        String systemPrompt = getSystemPrompt();
        StringBuilder history = new StringBuilder();
        for (AiConversationMessage msg : context) {
            String role = "user".equals(msg.getRole()) ? "用户" : "AI";
            history.append(role).append(": ").append(msg.getMessage()).append("\n");
        }

        // 4. 流式调用 AI（Flux 逐 token 推送，前端打字机）
        StringBuilder full = new StringBuilder();
        AppointmentTools.bindUser(userId);
        try {
            chatClient.prompt()
                .system(systemPrompt)
                .user(history.toString() + "\n用户: " + message)
                .tools(appointmentTools)
                .stream()
                .content()
                .doOnNext(chunk -> {
                    if (chunk != null && !chunk.isEmpty()) {
                        full.append(chunk);
                        try {
                            // 每个片段作为 SSE 的 data 事件推送
                            emitter.send(SseEmitter.event().name("message").data(chunk));
                        } catch (Exception e) {
                            // 客户端断开，忽略
                            throw new RuntimeException(e);
                        }
                    }
                })
                .doOnComplete(() -> {
                    try {
                        emitter.send(SseEmitter.event().name("done").data(full.toString()));
                        emitter.complete();
                    } catch (Exception ignored) {}
                })
                .doOnError(err -> {
                    try {
                        emitter.send(SseEmitter.event().name("error").data(err.getMessage() != null ? err.getMessage() : "AI回复失败"));
                        emitter.complete();
                    } catch (Exception ignored) {}
                })
                .blockLast(); // 阻塞直到流结束（同步执行）
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().name("error").data("AI调用异常"));
                emitter.complete();
            } catch (Exception ignored) {}
        } finally {
            AppointmentTools.clearUser();
        }

        // 5. 保存完整 AI 回复
        if (full.length() > 0) {
            AiConversationMessage assistantMsg = new AiConversationMessage();
            assistantMsg.setSessionId(sessionId);
            assistantMsg.setUserId(userId);
            assistantMsg.setRole("assistant");
            assistantMsg.setMessage(full.toString());
            assistantMsg.setCreateTime(LocalDateTime.now());
            assistantMsg.setUpdateTime(LocalDateTime.now());
            assistantMsg.setDeleted(0);
            messageMapper.insert(assistantMsg);
        }
    }

    /**
     * 从 SysConfig 获取系统提示词
     */
    private String getSystemPrompt() {
        SysConfig config = sysConfigMapper.findByKey("ai_chat_system_prompt");
        return config != null ? config.getConfigValue() : "你是一位专业的健康顾问。";
    }
}
