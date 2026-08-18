package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.dto.MessageSendDTO;
import com.wmm.eldercare.core.vo.MessageAdminListVO;

/**
 * 管理端站内消息 Service
 */
public interface MessageAdminService {

    /**
     * 分页查询消息列表（userId 为空查全部，非空按用户筛选）
     *
     * @param userId   筛选用户 ID（可空）
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<MessageAdminListVO> listMessages(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 发送消息（推送给指定用户）
     *
     * @param dto 消息内容（userId/title/content/type）
     */
    void sendMessage(MessageSendDTO dto);

    /**
     * 逻辑删除消息（管理端可删任意消息）
     *
     * @param messageId 消息 ID
     */
    void deleteMessage(Long messageId);
}