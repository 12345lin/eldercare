package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 系统配置实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysConfig {
    private Long id;                    // 配置 ID
    private String configKey;           // 配置键
    private String configValue;         // 配置值
    private String description;         // 配置描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
