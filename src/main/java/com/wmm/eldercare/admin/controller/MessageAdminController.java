package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.dto.MessageSendDTO;
import com.wmm.eldercare.core.service.MessageAdminService;
import com.wmm.eldercare.core.vo.MessageAdminListVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端站内消息 Controller
 */
@RestController
@RequestMapping("/api/admin/messages")
@Slf4j
@RequiredArgsConstructor
public class MessageAdminController {

    private final MessageAdminService messageAdminService;

    /**
     * 分页查询消息列表（可按用户筛选）
     * GET /api/admin/messages?userId=1&pageNum=1&pageSize=10
     */
    @GetMapping
    public Result<PageResult<MessageAdminListVO>> listMessages(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端查询消息列表: adminId={}, userId={}, pageNum={}, pageSize={}", adminId, userId, pageNum, pageSize);
        return Result.success(messageAdminService.listMessages(userId, pageNum, pageSize));
    }

    /**
     * 发送消息（推送给指定用户）
     * POST /api/admin/messages
     */
    @PostMapping
    public Result<Void> sendMessage(@RequestBody MessageSendDTO dto,
                                    HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端发送消息: adminId={}, toUserId={}, title={}", adminId, dto.getUserId(), dto.getTitle());
        messageAdminService.sendMessage(dto);
        return Result.success();
    }

    /**
     * 删除消息
     * DELETE /api/admin/messages/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id,
                                      HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端删除消息: adminId={}, messageId={}", adminId, id);
        messageAdminService.deleteMessage(id);
        return Result.success();
    }
}