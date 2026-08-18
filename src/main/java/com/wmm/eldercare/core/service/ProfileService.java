package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.dto.ChangePasswordDTO;
import com.wmm.eldercare.core.dto.ProfileUpdateDTO;
import com.wmm.eldercare.core.vo.ProfileStatsVO;
import com.wmm.eldercare.core.vo.ProfileVO;

/**
 * 个人中心 Service
 */
public interface ProfileService {

    /**
     * 查询我的个人资料
     *
     * @param userId 用户 ID
     * @return 个人资料
     */
    ProfileVO getProfile(Long userId);

    /**
     * 修改个人资料（只允许修改基础信息字段）
     *
     * @param userId 用户 ID
     * @param dto    修改内容（realName/gender/birthDate/height/avatar/emergencyContact）
     */
    void updateProfile(Long userId, ProfileUpdateDTO dto);

    /**
     * 修改密码（先校验旧密码正确，新密码 BCrypt 加密后存储）
     *
     * @param userId 用户 ID
     * @param dto    旧密码 + 新密码
     */
    void changePassword(Long userId, ChangePasswordDTO dto);

    /**
     * 查询个人中心统计面板（积分/健康记录数/评测次数/预约数/报名活动数/未读消息数）
     *
     * @param userId 用户 ID
     * @return 统计数据
     */
    ProfileStatsVO getStats(Long userId);
}