package com.wmm.eldercare.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    private String oldPassword;         // 旧密码（需校验正确）
    private String newPassword;         // 新密码（BCrypt 加密存储）
}