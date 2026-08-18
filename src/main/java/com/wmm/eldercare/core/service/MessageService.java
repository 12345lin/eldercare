package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.vo.MessageDetailVO;
import com.wmm.eldercare.core.vo.MessageListVO;

/**
 * 站内消息 Service
 */
public interface MessageService {

    /**
     * 分页查询我的消息列表（按创建时间倒序）
     *
     * @param userId   用户 ID
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<MessageListVO> listMessages(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询消息详情（查询后自动标记为已读）
     *
     * @param userId    用户 ID
     * @param messageId 消息 ID
     * @return 消息详情
     */
    MessageDetailVO getDetail(Long userId, Long messageId);

    /**
     * 标记单条消息已读
     *
     * @param userId    用户 ID
     * @param messageId 消息 ID
     */
    void markRead(Long userId, Long messageId);

    /**
     * 标记所有消息已读
     *
     * @param userId 用户 ID
     */
    void markAllRead(Long userId);

    /**
     * 逻辑删除消息（防越权：只能删自己的）
     *
     * @param userId    用户 ID
     * @param messageId 消息 ID
     */
    void deleteMessage(Long userId, Long messageId);

    /**
     * 查询未读消息数
     *
     * @param userId 用户 ID
     * @return 未读数
     */
    Integer countUnread(Long userId);
}