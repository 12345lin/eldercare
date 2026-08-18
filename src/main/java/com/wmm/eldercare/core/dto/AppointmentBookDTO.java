package com.wmm.eldercare.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预约体检 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookDTO {
    private Long slotId;  // 时段 ID
}
