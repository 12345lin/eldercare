package com.wmm.eldercare.core.service.impl;

import com.wmm.eldercare.core.mapper.DashboardMapper;
import com.wmm.eldercare.core.service.DashboardService;
import com.wmm.eldercare.core.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理端仪表盘 Service 实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardVO getDashboardData() {
        log.info("开始统计仪表盘数据");
        DashboardVO vo = new DashboardVO();
        vo.setTotalUsers(dashboardMapper.countTotalUsers());
        vo.setTodayNewUsers(dashboardMapper.countTodayNewUsers());
        vo.setEnabledUsers(dashboardMapper.countEnabledUsers());
        vo.setDisabledUsers(dashboardMapper.countDisabledUsers());
        vo.setTotalAppointments(dashboardMapper.countTotalAppointments());
        vo.setTodayAppointments(dashboardMapper.countTodayAppointments());
        vo.setPendingAppointments(dashboardMapper.countPendingAppointments());
        vo.setTotalActivities(dashboardMapper.countTotalActivities());
        vo.setActiveActivities(dashboardMapper.countActiveActivities());
        vo.setTotalHealthRecords(dashboardMapper.countTotalHealthRecords());
        vo.setUnreadMessages(dashboardMapper.countUnreadMessages());
        log.info("仪表盘数据统计完成");
        return vo;
    }
}
