package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.service.HealthRecordService;
import com.wmm.eldercare.core.service.UserService;
import com.wmm.eldercare.core.vo.HealthTrendVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端健康档案：查看某会员的健康记录列表与趋势
 * <p>对应详细设计 5.2「管理端职责」：会员健康档案查询 /api/admin/health-record</p>
 */
@RestController
@RequestMapping("/api/admin/health-record")
@Slf4j
@RequiredArgsConstructor
public class HealthRecordAdminController {

    private final HealthRecordService healthRecordService;
    private final UserService userService;

    /**
     * 分页查询某会员的健康记录
     * GET /api/admin/health-record?userId=2&pageNum=1&pageSize=10
     */
    @GetMapping
    public Result<PageResult<HealthRecord>> listByUser(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        checkUser(userId);
        PageResult<HealthRecord> result = healthRecordService.listHealthRecords(userId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 查询某会员的健康趋势（最近 6 个月）
     * GET /api/admin/health-record/trend?userId=2
     */
    @GetMapping("/trend")
    public Result<HealthTrendVO> trend(@RequestParam Long userId) {
        checkUser(userId);
        return Result.success(healthRecordService.getTrend(userId));
    }

    private void checkUser(Long userId) {
        if (userService.findUserById(userId) == null) {
            throw new BusinessException(404, "用户不存在");
        }
    }
}
