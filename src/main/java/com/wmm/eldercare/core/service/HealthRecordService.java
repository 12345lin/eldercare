package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.dto.HealthRecordAddDTO;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.vo.HealthTrendVO;

public interface HealthRecordService {

    /**
     * 添加健康记录（userId 从 token 获取，不信任前端传值）
     */
    void addHealthRecord(Long userId, HealthRecordAddDTO dto);

    /**
     * 分页查询某用户的健康记录
     */
    PageResult<HealthRecord> listHealthRecords(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询某用户某条健康记录详情
     */
    HealthRecord getHealthRecord(Long userId, Long id);

    /**
     * 逻辑删除某用户某条健康记录
     */
    void deleteHealthRecord(Long userId, Long id);

    /**
     * 健康趋势分析：按月份聚合最近 6 个月的平均值/最大值/最小值
     */
    HealthTrendVO getTrend(Long userId);
}