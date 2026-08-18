package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站内消息 Mapper
 */
@Mapper
public interface MessageMapper {

    /**
     * 插入消息（管理端推送）
     */
    int insert(Message message);

    /**
     * 分页查询某用户的消息列表（按创建时间倒序）
     */
    List<Message> findByUserId(@Param("userId") Long userId);

    /**
     * 根据 ID 查询消息（带归属校验，防越权）
     */
    Message findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 统计某用户未读消息数
     */
    int countUnread(@Param("userId") Long userId);

    /**
     * 标记单条消息已读（防越权：只能标记自己的）
     */
    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 标记某用户全部消息已读
     */
    int markAllRead(@Param("userId") Long userId);

    /**
     * 逻辑删除消息（防越权：只能删自己的）
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}