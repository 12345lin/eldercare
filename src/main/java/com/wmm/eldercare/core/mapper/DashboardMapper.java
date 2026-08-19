package com.wmm.eldercare.core.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 仪表盘统计 Mapper
 */
@Mapper
public interface DashboardMapper {

    // ========== 用户统计 ==========
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0")
    long countTotalUsers();

    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 AND DATE(create_time) = CURDATE()")
    long countTodayNewUsers();

    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 AND status = 'ENABLED'")
    long countEnabledUsers();

    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 AND status = 'DISABLED'")
    long countDisabledUsers();

    // ========== 预约统计 ==========
    @Select("SELECT COUNT(*) FROM appointment WHERE deleted = 0")
    long countTotalAppointments();

    @Select("SELECT COUNT(*) FROM appointment WHERE deleted = 0 AND DATE(create_time) = CURDATE()")
    long countTodayAppointments();

    @Select("SELECT COUNT(*) FROM appointment WHERE deleted = 0 AND status = 'PENDING'")
    long countPendingAppointments();

    // ========== 活动统计 ==========
    @Select("SELECT COUNT(*) FROM community_activity WHERE deleted = 0")
    long countTotalActivities();

    @Select("SELECT COUNT(*) FROM community_activity WHERE deleted = 0 AND status = 'ONGOING'")
    long countActiveActivities();

    // ========== 健康记录统计 ==========
    @Select("SELECT COUNT(*) FROM health_record WHERE deleted = 0")
    long countTotalHealthRecords();

    // ========== 消息统计 ==========
    @Select("SELECT COUNT(*) FROM message WHERE deleted = 0 AND is_read = 0")
    long countUnreadMessages();
}
