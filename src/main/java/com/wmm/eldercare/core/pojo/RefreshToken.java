package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 刷新令牌实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private Long id;                    // 主键ID
    private Long userId;                // 用户ID
    private String token;               // 刷新令牌
    private LocalDateTime expireTime;           // 过期时间
    private LocalDateTime createTime;           // 创建时间
}