package com.wmm.eldercare.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 体检套餐详情 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPackageVO {
    private Long id;                          // 套餐 ID
    private String name;                      // 套餐名称
    private String coverUrl;                  // 封面图 URL
    private String description;               // 套餐描述
    private Integer price;                    // 价格（积分）
    private String suitablePeople;            // 适合人群
    private String items;                     // 包含项目列表 JSON
    private String status;                    // 状态
    private LocalDateTime createTime;         // 创建时间
}
