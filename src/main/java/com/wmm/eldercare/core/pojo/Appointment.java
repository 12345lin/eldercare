package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 预约记录实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private Long id;                    // 预约 ID
    private Long userId;                // 用户 ID
    private Long slotId;                // 时间段 ID
    private Long packageId;             // 套餐 ID
    private String status;              // 状态：PENDING/CONFIRMED/CANCELED/COMPLETED
    private String reportUrl;           // 体检报告 URL
    private String originalFilename;    // 体检报告原始文件名
    private LocalDateTime reportUploadTime; // 报告上传时间
    private Long uploadAdminId;         // 上传管理员 ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
