package com.wmm.eldercare.admin.controller;


import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/points")
@Slf4j
@RequiredArgsConstructor
public class PointsAdminController {
    private final UserService userService;

    /**
     * 调整用户积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @param type 积分类型
     * @param reason 调整原因
     * @return
     */
    @PostMapping("/adjust")
    public Result<Integer> adjustPoints(@RequestParam Long userId, @RequestParam Integer amount,
                                        @RequestParam String type, @RequestParam String reason) {
        int balance = userService.adjustPoints(userId, amount, type, reason);
        return Result.success(balance);
    }
}
