package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.service.MessageService;
import com.wmm.eldercare.core.vo.MessageDetailVO;
import com.wmm.eldercare.core.vo.MessageListVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 站内消息 Controller（会员端）
 */
@RestController
@RequestMapping("/api/messages")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 分页查询我的消息列表
     * GET /api/messages?pageNum=1&pageSize=10
     */
    @GetMapping
    public Result<PageResult<MessageListVO>> listMessages(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询消息列表: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);
        return Result.success(messageService.listMessages(userId, pageNum, pageSize));
    }

    /**
     * 查询消息详情（自动标记已读）
     * GET /api/messages/{id}
     */
    @GetMapping("/{id}")
    public Result<MessageDetailVO> getDetail(@PathVariable Long id,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询消息详情: userId={}, messageId={}", userId, id);
        return Result.success(messageService.getDetail(userId, id));
    }

    /**
     * 标记单条消息已读
     * PUT /api/messages/{id}/read
     */
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id,
                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("标记已读: userId={}, messageId={}", userId, id);
        messageService.markRead(userId, id);
        return Result.success();
    }

    /**
     * 标记全部消息已读
     * PUT /api/messages/read-all
     */
    @PutMapping("/read-all")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("全部已读: userId={}", userId);
        messageService.markAllRead(userId);
        return Result.success();
    }

    /**
     * 删除消息（逻辑删除）
     * DELETE /api/messages/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除消息: userId={}, messageId={}", userId, id);
        messageService.deleteMessage(userId, id);
        return Result.success();
    }

    /**
     * 查询未读消息数
     * GET /api/messages/unread-count
     */
    @GetMapping("/unread-count")
    public Result<Integer> countUnread(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询未读消息数: userId={}", userId);
        return Result.success(messageService.countUnread(userId));
    }
}