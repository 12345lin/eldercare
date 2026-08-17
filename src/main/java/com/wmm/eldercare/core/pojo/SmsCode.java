package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 短信验证码实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsCode {
    private Long id;                    // 记录 ID
    private String phone;               // 手机号
    private String code;                // 验证码
    private LocalDateTime expireTime;   // 过期时间
    private Integer used;               // 是否已使用：0 未使用/1 已使用
    private LocalDateTime createTime;   // 创建时间
}
