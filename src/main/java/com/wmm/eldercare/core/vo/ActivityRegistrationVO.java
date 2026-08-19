package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动报名名单 VO（管理端查看，含用户手机号/姓名）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRegistrationVO {
    private Long id;                 // 报名记录 ID
    private Long userId;             // 用户 ID
    private String phone;            // 手机号
    private String realName;         // 真实姓名
    private String checkInStatus;    // 签到状态：NOT_CHECKED_IN / CHECKED_IN
    private String createTime;       // 报名时间
}