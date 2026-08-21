package com.wmm.eldercare.core.task;

import com.wmm.eldercare.core.mapper.PointTransactionMapper;
import com.wmm.eldercare.core.mapper.SmsCodeMapper;
import com.wmm.eldercare.core.mapper.UserMapper;
import com.wmm.eldercare.core.pojo.PointTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统定时任务
 * <p>对应详细设计 5.12 定时任务列表。已实现：清理过期验证码、清理过期积分。</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private final SmsCodeMapper smsCodeMapper;
    private final PointTransactionMapper pointTransactionMapper;
    private final UserMapper userMapper;

    /**
     * 清理过期短信验证码：每 10 分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void cleanExpiredSmsCode() {
        try {
            int rows = smsCodeMapper.deleteExpired();
            if (rows > 0) {
                log.info("定时任务：清理过期短信验证码 {} 条", rows);
            }
        } catch (Exception e) {
            log.error("清理过期短信验证码失败", e);
        }
    }

    /**
     * 清理过期积分：每天凌晨 2 点。将已过期且未消费的积分批次清零，并同步扣减用户积分。
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredPoints() {
        try {
            List<PointTransaction> expired = pointTransactionMapper.findExpired(LocalDateTime.now());
            if (expired.isEmpty()) return;
            int total = 0;
            for (PointTransaction batch : expired) {
                int amount = batch.getRemainAmount();
                if (amount <= 0) continue;
                // 扣除用户积分（原子）
                userMapper.deductPoints(batch.getUserId(), amount);
                // 清零批次 remain_amount
                pointTransactionMapper.reduceRemain(batch.getId(), amount);
                // 生成过期流水
                com.wmm.eldercare.core.pojo.PointTransaction expireTx = new com.wmm.eldercare.core.pojo.PointTransaction();
                expireTx.setUserId(batch.getUserId());
                expireTx.setType("EXPIRE");
                expireTx.setChangeAmount(-amount);
                expireTx.setBalanceAfter(userMapper.selectPoints(batch.getUserId()));
                expireTx.setRemainAmount(0);
                expireTx.setBatchTxId(batch.getId());
                expireTx.setDescription("积分过期清理");
                expireTx.setCreateTime(LocalDateTime.now());
                expireTx.setUpdateTime(LocalDateTime.now());
                expireTx.setDeleted(0);
                pointTransactionMapper.insert(expireTx);
                total += amount;
            }
            if (total > 0) {
                log.info("定时任务：清理过期积分 {} 分，涉及 {} 批次", total, expired.size());
            }
        } catch (Exception e) {
            log.error("清理过期积分失败", e);
        }
    }
}
