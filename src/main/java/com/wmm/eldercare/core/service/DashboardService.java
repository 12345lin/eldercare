package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.vo.DashboardVO;

/**
 * 管理端仪表盘 Service 接口
 */
public interface DashboardService {
    /**
     * 获取仪表盘统计数据
     */
    DashboardVO getDashboardData();
}
