package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.PointTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 积分流水 Mapper
 */
@Mapper
public interface PointTransactionMapper {

    int insert(PointTransaction tx);

    /**
     * 查询某用户所有未删除的积分流水（按时间倒序）
     */
    List<PointTransaction> findByUserId(@Param("userId") Long userId);

    /**
     * 查询某用户最早的可用（未过期、有剩余）获得批次，用于 FIFO 消费
     */
    List<PointTransaction> findAvailableBatches(@Param("userId") Long userId);

    /**
     * 扣减某获得批次的剩余积分（remain_amount 原子扣减）
     */
    int reduceRemain(@Param("id") Long id, @Param("amount") Integer amount);

    /**
     * 更新某流水（用于取消预约时还原：batch.remain 加回）
     */
    int updateRemainAdd(@Param("id") Long id, @Param("amount") Integer amount);

    /**
     * 逻辑删除某流水（取消预约时标记消费流水删除）
     */
    int deleteTx(@Param("id") Long id);

    /**
     * 查询已过期且有剩余积分的批次（积分过期清理任务）
     */
    List<PointTransaction> findExpired(@Param("now") java.time.LocalDateTime now);
}
