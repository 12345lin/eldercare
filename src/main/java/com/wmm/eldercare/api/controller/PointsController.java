package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.UserPointRecord;
import com.wmm.eldercare.core.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@Slf4j
@RequiredArgsConstructor
public class PointsController {

    private final UserService userService;

    /**
     * 获取用户积分
     */
    @GetMapping("/points")
    public Result getPoints(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("getPoints, userId: {}", userId);
        Integer points = userService.getPoints(userId);
        return Result.success(points);
    }

    /**
     * 分页查询用户积分记录
     * @param request 请求
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页查询结果
     */
    @GetMapping("/points/records")
    public Result<PageResult<UserPointRecord>> getPointsRecords(HttpServletRequest request,
                                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("分页查询用户积分记录, userId: {}, pageNum: {}, pageSize: {}", userId, pageNum, pageSize);
        PageResult<UserPointRecord> pageResult = userService.listPointRecords(userId, pageNum, pageSize);
        return Result.success(pageResult);
    }
}