package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPointRecord {
    private Long id;
    private Long userId;
    private String type;       // REGISTER / ACTIVITY_CHECKIN / ASSESSMENT / APPOINTMENT / ADJUST / CANCEL
    private Integer amount;    // 正数=增加，负数=扣减
    private Integer balance;   // 变动后的余额
    private String reason;     // 变动原因
    private LocalDateTime createTime;
    private Integer deleted;
}
