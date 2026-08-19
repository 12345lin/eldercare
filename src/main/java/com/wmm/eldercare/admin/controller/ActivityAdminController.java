package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.CommunityActivity;
import com.wmm.eldercare.core.service.ActivityAdminService;
import com.wmm.eldercare.core.vo.ActivityRegistrationVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/activities")
@Slf4j
@RequiredArgsConstructor
public class ActivityAdminController {
    private final ActivityAdminService activityAdminService;

    /**
     * 分页查询活动列表（可按标题搜索、状态筛选）
     * GET /api/admin/activities?keyword=x&status=REGISTRATING
     */
    @GetMapping
    public Result<PageResult<CommunityActivity>> listActivities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询活动列表: userId={}, keyword={}, status={}, pageNum={}, pageSize={}",
                userId, keyword, status, pageNum, pageSize);
        return Result.success(activityAdminService.listActivities(keyword, status, pageNum, pageSize));
    }

    /**
     * 查询活动详情
     * GET /api/admin/activities/{id}
     */
    @GetMapping("/{id}")
    public Result<CommunityActivity> getActivity(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询活动详情: userId={}, activityId={}", userId, id);
        return Result.success(activityAdminService.getActivity(id));
    }

    /**
     * 新增活动
     * POST /api/admin/activities
     */
    @PostMapping
    public Result<Void> addActivity(@RequestBody CommunityActivity communityActivity, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("新增活动: userId={}, title={}", userId, communityActivity.getTitle());
        activityAdminService.addActivity(communityActivity);
        return Result.success();
    }

    /**
     * 修改活动
     * PUT /api/admin/activities/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateActivity(@PathVariable Long id,
                                       @RequestBody CommunityActivity communityActivity,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("修改活动: userId={}, activityId={}", userId, id);
        activityAdminService.updateActivity(id, communityActivity);
        return Result.success();
    }

    /**
     * 活动状态流转
     * PUT /api/admin/activities/{id}/status?status=REGISTRATING
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateActivityStatus(@PathVariable Long id,
                                             @RequestParam String status,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("活动状态流转: userId={}, activityId={}, status={}", userId, id, status);
        activityAdminService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除活动
     * DELETE /api/admin/activities/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除活动: userId={}, activityId={}", userId, id);
        activityAdminService.deleteActivity(id);
        return Result.success();
    }

    /**
     * 查询活动报名名单（含手机号/姓名/签到状态）
     * GET /api/admin/activities/{id}/registrations
     */
    @GetMapping("/{id}/registrations")
    public Result<List<ActivityRegistrationVO>> findRegistrations(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询活动报名名单: userId={}, activityId={}", userId, id);
        return Result.success(activityAdminService.listRegistrations(id));
    }
}