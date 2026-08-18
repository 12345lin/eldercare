package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.UserPointRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserPointRecordMapper {
    /**
     * 插入用户积分记录
     * @param record
     * @return
     */
    int insert(UserPointRecord record);

    /**
     * 查询用户积分记录列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<UserPointRecord> listByUserId(@Param("userId") Long userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 查询用户积分记录数量
     * @param userId
     * @return
     */
    int countByUserId(@Param("userId") Long userId);
    /**
     * 查询指定时间前的有效积分记录（用于过期清理）
     * @param expireTime
     * @return
     */
    List<UserPointRecord> findExpiredRecords(@Param("expireTime") java.time.LocalDateTime expireTime);
}
