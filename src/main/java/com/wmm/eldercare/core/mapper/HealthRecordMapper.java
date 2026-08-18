package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.vo.MonthlyHealthStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HealthRecordMapper {

    /**
     * 插入健康记录
     */
    int insert(HealthRecord healthRecord);

    /**
     * 查询某用户的健康记录列表（按记录时间倒序，分页由 PageHelper 处理）
     */
    List<HealthRecord> findByUserId(Long userId);

    /**
     * 统计某用户健康记录总数（个人中心统计面板）
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 查询某用户某条健康记录（userId 防止越权查看别人的记录）
     */
    HealthRecord findById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 逻辑删除某用户某条健康记录
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 按月份聚合统计某用户最近 N 个月的健康指标（平均值/最大值/最小值）
     * @param userId     用户 ID
     * @param monthCount 统计最近几个月（如 6 = 最近 6 个月）
     */
    List<MonthlyHealthStatVO> selectMonthlyStats(@Param("userId") Long userId, @Param("monthCount") Integer monthCount);
}