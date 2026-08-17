package com.wmm.eldercare.core.dto;

import lombok.Data;

/**
 * 重置密码 DTO
 */
@Data
public class ResetPasswordDTO {
    private String phone;
    private String password;
    private String confirmPassword;
    private String code;
}
