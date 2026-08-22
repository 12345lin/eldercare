package com.wmm.eldercare.core.service.impl;

import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.dto.ChangePasswordDTO;
import com.wmm.eldercare.core.dto.ProfileUpdateDTO;
import com.wmm.eldercare.core.mapper.*;
import com.wmm.eldercare.core.pojo.Appointment;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.service.ProfileService;
import com.wmm.eldercare.core.vo.ProfileStatsVO;
import com.wmm.eldercare.core.vo.ProfileVO;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ActivityRegistrationMapper activityRegistrationMapper;
    private final AppointmentMapper appointmentMapper;
    private final AssessmentResultMapper assessmentResultMapper;
    private final HealthRecordMapper healthRecordMapper;
    private final MessageMapper messageMapper;

    /**
     * 查询我的个人资料
     *
     * @param userId 用户 ID
     * @return 个人资料
     */
    @Override
    public ProfileVO getProfile(Long userId) {
        //1.根据用户 ID 查询用户信息
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        ProfileVO profileVO = new ProfileVO();
        profileVO.setId(user.getId());
        profileVO.setPhone(user.getPhone());
        profileVO.setRealName(user.getRealName());
        profileVO.setGender(user.getGender());
        profileVO.setBirthDate(user.getBirthDate());
        profileVO.setHeight(user.getHeight());
        profileVO.setAvatar(user.getAvatar());
        profileVO.setEmergencyContact(user.getEmergencyContact());
        profileVO.setMemberLevel(user.getMemberLevel());
        profileVO.setPoints(user.getPoints());
        profileVO.setCreateTime(user.getCreateTime().toString());
        return profileVO;
    }

    /**
     * 更新我的个人资料
     *
     * @param userId 用户 ID
     * @param dto    修改内容（realName/gender/birthDate/height/avatar/emergencyContact）
     */
    @Override
    public void updateProfile(Long userId, ProfileUpdateDTO dto) {
        //1.根据用户 ID 查询用户信息
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        //2.更改用户信息
        user.setRealName(dto.getRealName());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.getBirthDate());
        user.setHeight(dto.getHeight());
        user.setAvatar(dto.getAvatar());
        user.setEmergencyContact(dto.getEmergencyContact());
        //3.更新用户信息
        int rows = userMapper.updateUser(userId, user);
        if (rows == 0) {
            throw new BusinessException(400, "更新失败");
        }
    }

    /**
     * 修改密码
     *
     * @param userId 用户 ID
     * @param dto    旧密码 + 新密码
     */
    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        //1.根据用户 ID 查询用户信息
        User user = userMapper.findByIdWithPassword(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        //2.校验旧密码是否正确
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        //3.更新用户信息
        int rows = userMapper.updateUser(userId, user);
        if (rows == 0) {
            throw new BusinessException(400, "更新失败");
        }
    }

    /**
     * 查询个人中心统计面板（积分/健康记录数/评测次数/预约数/报名活动数/未读消息数）
     *
     * @param userId 用户 ID
     * @return 统计数据
     */
    @Override
    public ProfileStatsVO getStats(Long userId) {
        //1.根据用户 ID 查询用户信息
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        ProfileStatsVO profileStatsVO = new ProfileStatsVO();
        //2.查询用户健康记录数
        profileStatsVO.setHealthRecordCount(healthRecordMapper.countByUserId(userId));
        //3.查询用户评测次数
        profileStatsVO.setAssessmentCount(assessmentResultMapper.countByUserId(userId));
        //4.查询用户预约数
        profileStatsVO.setAppointmentCount(appointmentMapper.countByUserId(userId));
        //5.查询用户报名活动数
        profileStatsVO.setActivityCount(activityRegistrationMapper.countByUserId(userId));
        //6.查询用户未读消息数
        profileStatsVO.setUnreadMessageCount(messageMapper.countUnread(userId));
        //7.查询用户积分
        profileStatsVO.setPoints(userMapper.selectPoints(userId));
        return profileStatsVO;
    }
}
