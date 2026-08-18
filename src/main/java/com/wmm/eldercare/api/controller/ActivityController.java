package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.service.ActivityService;
import com.wmm.eldercare.core.vo.ActivityDetailVO;
import com.wmm.eldercare.core.vo.ActivityListVO;
import com.wmm.eldercare.core.vo.MyActivityVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@Slf4j
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 1. 查询可报名活动列表
     * GET /api/activities
     */
    @GetMapping
    public Result<List<ActivityListVO>> listActivities(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询活动列表: userId={}", userId);
        return Result.success(activityService.listAvailable(userId));
    }

    /**
     * 2. 查询活动详情
     * GET /api/activities/{id}
     */
    @GetMapping("/{id}")
    public Result<ActivityDetailVO> getDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询活动详情: userId={}, activityId={}", userId, id);
        return Result.success(activityService.getDetail(userId, id));
    }

    /**
     * 3. 报名活动
     * POST /api/activities/{id}/register
     */
    @PostMapping("/{id}/register")
    public Result<Long> register(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("报名活动: userId={}, activityId={}", userId, id);
        Long registrationId = activityService.register(userId, id);
        return Result.success(registrationId);
    }

    /**
     * 4. 取消报名
     * DELETE /api/activities/{id}/register
     */
    @DeleteMapping("/{id}/register")
    public Result<Void> cancelRegister(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("取消报名: userId={}, activityId={}", userId, id);
        activityService.cancelRegister(userId, id);
        return Result.success();
    }

    /**
     * 5. 查询我的活动(已报名)
     * GET /api/activities/my
     */
    @GetMapping("/my")
    public Result<List<MyActivityVO>> listMyActivities(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我的活动: userId={}", userId);
        return Result.success(activityService.listMyActivities(userId));
    }

    /**
     * 6. 签到
     * POST /api/activities/{id}/check-in
     */
    @PostMapping("/{id}/check-in")
    public Result<Void> checkIn(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("活动签到: userId={}, activityId={}", userId, id);
        activityService.checkIn(userId, id);
        return Result.success();
    }
}