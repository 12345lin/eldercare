package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.service.DashboardService;
import com.wmm.eldercare.core.vo.DashboardVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端仪表盘 Controller
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     * GET /api/admin/dashboard
     */
    @GetMapping
    public Result<DashboardVO> getDashboard(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端获取仪表盘数据: adminId={}", adminId);
        return Result.success(dashboardService.getDashboardData());
    }
}
