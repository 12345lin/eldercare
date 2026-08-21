package com.wmm.eldercare.core.vo;

import com.wmm.eldercare.core.pojo.Appointment;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.pojo.PointTransaction;
import com.wmm.eldercare.core.pojo.User;
import lombok.Data;

import java.util.List;

/**
 * 管理端会员详情 VO —— 用户 + 最近健康记录 + 最近预约 + 最近积分流水
 */
@Data
public class AdminMemberDetailVO {
    private User user;
    private List<HealthRecord> recentHealthRecords;
    private List<Appointment> recentAppointments;
    private List<PointTransaction> recentPointTransactions;
}
