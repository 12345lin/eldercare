package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 个人资料 VO（我的资料展示）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVO {
    private Long id;                    // 用户 ID
    private String phone;               // 手机号（不可修改，只展示）
    private String realName;            // 真实姓名
    private String gender;              // 性别
    private LocalDate birthDate;        // 出生日期
    private BigDecimal height;          // 身高（cm）
    private String avatar;              // 头像 URL
    private String emergencyContact;    // 紧急联系人电话
    private String memberLevel;         // 会员等级
    private Integer points;             // 积分
    private String createTime;          // 注册时间
}