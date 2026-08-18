package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.AiConversationSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiConversationSessionMapper {

    /**
     * 插入会话
     */
    int insert(AiConversationSession session);

    /**
     * 根据 ID 查询会话
     */
    AiConversationSession findById(@Param("id") Long id);

    /**
     * 查询某用户的所有会话（按创建时间倒序）
     */
    List<AiConversationSession> findByUserId(@Param("userId") Long userId);

    /**
     * 逻辑删除会话
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
