package com.wmm.eldercare.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 修改个人资料 DTO
 *
 * <p>只允许修改个人基础信息，手机号/积分/等级/角色等敏感字段不可修改。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {
    private String realName;            // 真实姓名
    private String gender;              // 性别
    private LocalDate birthDate;        // 出生日期
    private BigDecimal height;          // 身高（cm）
    private String avatar;              // 头像 URL
    private String emergencyContact;    // 紧急联系人电话
}