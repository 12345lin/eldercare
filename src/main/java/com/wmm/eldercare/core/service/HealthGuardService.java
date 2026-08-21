package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.pojo.HealthRecord;

/**
 * 健康提醒 / 健康指导服务
 * <p>对应详细设计 4.3.4：录入健康数据后，指标超出正常范围时生成健康提醒，
 * 调用 AI 生成个性化建议，通过站内消息送达用户；同一指标当日只推送一次。</p>
 */
public interface HealthGuardService {

    /**
     * 录入健康记录后触发——检查指标是否超标，超标则生成健康提醒。
     *
     * @param userId      用户 ID
     * @param record      刚录入的健康记录（含各指标）
     */
    void checkAndNotify(Long userId, HealthRecord record);
}
