package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.AiConversationMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiConversationMessageMapper {

    /**
     * 插入消息
     */
    int insert(AiConversationMessage message);

    /**
     * 根据会话 ID 查询所有消息（按创建时间升序）
     */
    List<AiConversationMessage> findBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据会话 ID 逻辑删除所有消息
     */
    int deleteBySessionId(@Param("sessionId") Long sessionId);
}
