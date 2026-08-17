package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.dto.HealthRecordAddDTO;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.service.HealthRecordService;
import com.wmm.eldercare.core.vo.HealthTrendVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-records")
@Slf4j
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    /**
     * 添加健康记录（会员端）
     */
    @PostMapping
    public Result<Void> addHealthRecord(@RequestBody HealthRecordAddDTO dto,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("添加健康记录: userId={}", userId);
        healthRecordService.addHealthRecord(userId, dto);
        return Result.success();
    }

    /**
     * 分页查询我的健康记录
     */
    @GetMapping
    public Result<PageResult<HealthRecord>> listHealthRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("分页查询健康记录: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);
        PageResult<HealthRecord> result = healthRecordService.listHealthRecords(userId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 健康趋势分析（最近 6 个月，平均值/最大值/最小值）
     */
    @GetMapping("/trend")
    public Result<HealthTrendVO> getHealthTrend(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("健康趋势查询: userId={}", userId);
        return Result.success(healthRecordService.getTrend(userId));
    }

    /**
     * 查看健康记录详情
     */
    @GetMapping("/{id}")
    public Result<HealthRecord> getHealthRecord(@PathVariable Long id,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查看健康记录详情: userId={}, id={}", userId, id);
        HealthRecord record = healthRecordService.getHealthRecord(userId, id);
        return Result.success(record);
    }

    /**
     * 删除健康记录（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteHealthRecord(@PathVariable Long id,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除健康记录: userId={}, id={}", userId, id);
        healthRecordService.deleteHealthRecord(userId, id);
        return Result.success();
    }
}