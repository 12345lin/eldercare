package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 积分流水实体类（point_transaction）
 * <p>对应详细设计 6.3.18：记录每笔积分变动，支撑 FIFO 消费与积分过期。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointTransaction {
    private Long id;                 // 流水 ID
    private Long userId;             // 用户 ID
    private String type;             // 类型：REGISTRATION/CHECK_IN/ASSESSMENT/APPOINTMENT/ADMIN/EXPIRE
    private Integer changeAmount;    // 变动积分（正=获得，负=扣减）
    private Integer balanceAfter;    // 变动后积分余额
    private Integer remainAmount;    // 获得类流水的剩余可用积分（消费按 FIFO 扣减）
    private LocalDateTime expireTime; // 过期时间（获得时间+1年），消耗类为空
    private Long batchTxId;          // 消耗类流水关联的被扣获得批次 ID
    private String description;      // 业务说明
    private Long refId;              // 关联业务记录 ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;         // 逻辑删除
}
