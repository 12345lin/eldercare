package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.AiConversationMessage;
import com.wmm.eldercare.core.pojo.AiConversationSession;
import com.wmm.eldercare.core.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 创建对话会话
     */
    @PostMapping
    public Result<Long> createSession(@RequestParam(required = false) String sessionName,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("创建会话: userId={}, sessionName={}", userId, sessionName);
        Long sessionId = chatService.createSession(userId, sessionName);
        return Result.success(sessionId);
    }

    /**
     * 查询我的对话会话列表
     */
    @GetMapping
    public Result<List<AiConversationSession>> listSessions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询会话列表: userId={}", userId);
        List<AiConversationSession> sessions = chatService.findBySessionsByUserId(userId);
        return Result.success(sessions);
    }

    /**
     * 删除对话会话
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSession(@PathVariable Long id,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除会话: userId={}, sessionId={}", userId, id);
        chatService.deleteById(userId, id);
        return Result.success();
    }

    /**
     * 查询某会话的对话历史
     */
    @GetMapping("/{sessionId}/messages")
    public Result<List<AiConversationMessage>> getMessages(@PathVariable Long sessionId,
                                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询消息历史: userId={}, sessionId={}", userId, sessionId);
        List<AiConversationMessage> messages = chatService.getMessages(userId, sessionId);
        return Result.success(messages);
    }

    /**
     * 发送消息（调用 AI 回复）
     */
    @PostMapping("/{sessionId}/send")
    public Result<String> sendMessage(@PathVariable Long sessionId,
                                      @RequestBody MessageDTO dto,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("发送消息: userId={}, sessionId={}, message={}", userId, sessionId, dto.getMessage());
        String aiResponse = chatService.sendMessage(userId, sessionId, dto.getMessage());
        return Result.success(aiResponse);
    }

    /**
     * 流式发送消息（SSE，打字机效果）
     * POST /api/chats/{sessionId}/stream
     */
    @PostMapping(value = "/{sessionId}/stream", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter sendMessageStream(@PathVariable Long sessionId,
                                        @RequestBody MessageDTO dto,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("流式发送消息: userId={}, sessionId={}", userId, sessionId);
        SseEmitter emitter = new SseEmitter(120_000L); // 2分钟超时
        // 异步执行，避免阻塞请求线程
        new Thread(() -> chatService.sendMessageStream(userId, sessionId, dto.getMessage(), emitter)).start();
        return emitter;
    }

    /**
     * 消息请求体 DTO（内部类）
     */
    public static class MessageDTO {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
