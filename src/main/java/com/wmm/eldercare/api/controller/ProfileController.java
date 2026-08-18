package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.dto.ChangePasswordDTO;
import com.wmm.eldercare.core.dto.ProfileUpdateDTO;
import com.wmm.eldercare.core.service.ProfileService;
import com.wmm.eldercare.core.vo.ProfileStatsVO;
import com.wmm.eldercare.core.vo.ProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    /**
     * 查询我的个人资料
     *
     * @param request
     * @return
     */
    @GetMapping
    public Result<ProfileVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ProfileVO profile = profileService.getProfile(userId);
        return Result.success(profile);
    }

    /**
     * 更新我的个人资料
     *
     * @param dto
     * @param request
     * @return
     */
    @PutMapping
    public Result<Void> changeProfile(@RequestBody ProfileUpdateDTO dto,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        profileService.updateProfile(userId, dto);
        return Result.success();
    }

    /**
     * 修改密码
     * @param dto
     * @param request
     * @return
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody ChangePasswordDTO dto,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        profileService.changePassword(userId, dto);
        return Result.success();
    }

    /**
     * 查询个人中心统计面板（积分/健康记录数/评测次数/预约数/报名活动数/未读消息数）
     * @param request
     * @return
     */
    @GetMapping("/stats")
    public Result<ProfileStatsVO> getProfileStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ProfileStatsVO stats = profileService.getStats(userId);
        return Result.success(stats);
    }
}
