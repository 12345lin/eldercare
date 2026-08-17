package com.wmm.eldercare.core.dto;

import lombok.Data;

/**
 * 注册 DTO
 */
@Data
public class RegisterDTO {
    private String phone;
    private String password;
    private String realName;
    private String smsCode;
}
