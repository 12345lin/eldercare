package com.wmm.eldercare.core.service.impl;

import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.mapper.ActivityRegistrationMapper;
import com.wmm.eldercare.core.mapper.CommunityActivityMapper;
import com.wmm.eldercare.core.mapper.SysConfigMapper;
import com.wmm.eldercare.core.pojo.ActivityRegistration;
import com.wmm.eldercare.core.pojo.CommunityActivity;
import com.wmm.eldercare.core.pojo.SysConfig;
import com.wmm.eldercare.core.service.ActivityService;
import com.wmm.eldercare.core.service.UserService;
import com.wmm.eldercare.core.vo.ActivityDetailVO;
import com.wmm.eldercare.core.vo.ActivityListVO;
import com.wmm.eldercare.core.vo.MyActivityVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final CommunityActivityMapper activityMapper;
    private final ActivityRegistrationMapper registrationMapper;
    private final SysConfigMapper sysConfigMapper;
    private final UserService userService;

    /**
     * 1. 查询可报名活动列表
     */
    @Override
    public List<ActivityListVO> listAvailable(Long userId) {
        List<CommunityActivity> activities = activityMapper.listAvailable();
        return activities.stream().map(activity -> {
            ActivityRegistration registration = registrationMapper.findByActivityIdAndUserId(
                    activity.getId(), userId);
            Boolean registered = registration != null;
            return new ActivityListVO(
                    activity.getId(),
                    activity.getTitle(),
                    activity.getCoverUrl(),
                    activity.getActivityStart(),
                    activity.getActivityEnd(),
                    activity.getMaxParticipants(),
                    activity.getCurrentParticipants(),
                    activity.getStatus(),
                    registered
            );
        }).collect(Collectors.toList());
    }

    /**
     * 2. 查询活动详情
     */
    @Override
    public ActivityDetailVO getDetail(Long userId, Long activityId) {
        // 1. 校验活动是否存在
        CommunityActivity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 2. 查询用户是否已报名
        ActivityRegistration registration = registrationMapper.findByActivityIdAndUserId(
                activityId, userId);
        Boolean registered = registration != null;
        String checkInStatus = registered && !"NOT_CHECKED_IN".equals(registration.getCheckInStatus())
                ? registration.getCheckInStatus() : null;

        // 3. 组装 VO
        return new ActivityDetailVO(
                activity.getId(),
                activity.getTitle(),
                activity.getCoverUrl(),
                activity.getContent(),
                activity.getRegistrationStart(),
                activity.getRegistrationEnd(),
                activity.getActivityStart(),
                activity.getActivityEnd(),
                activity.getMaxParticipants(),
                activity.getCurrentParticipants(),
                activity.getStatus(),
                registered,
                checkInStatus
        );
    }

    /**
     * 3. 报名活动(并发控制 + 防重复)
     */
    @Transactional
    @Override
    public Long register(Long userId, Long activityId) {
        // 1. 校验活动是否存在
        CommunityActivity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 2. 校验活动状态是否在报名中
        if (!"REGISTRATING".equals(activity.getStatus())) {
            throw new BusinessException(400, "活动未开放报名");
        }

        // 3. 校验报名时间段
        LocalDateTime now = LocalDateTime.now();
        if (activity.getRegistrationStart() != null && now.isBefore(activity.getRegistrationStart())) {
            throw new BusinessException(400, "报名未开始");
        }
        if (activity.getRegistrationEnd() != null && now.isAfter(activity.getRegistrationEnd())) {
            throw new BusinessException(400, "报名已结束");
        }

        // 4. 校验是否已报名(防重复)
        ActivityRegistration existing = registrationMapper.findByActivityIdAndUserId(activityId, userId);
        if (existing != null) {
            throw new BusinessException(400, "已报名该活动，请勿重复报名");
        }

        // 5. 并发控制：原子增加名额
        int rows = activityMapper.incrementParticipants(activityId);
        if (rows == 0) {
            throw new BusinessException(400, "活动名额已满");
        }

        // 6. 插入报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setUserId(userId);
        registration.setActivityId(activityId);
        registration.setCheckInStatus("NOT_CHECKED_IN");
        registration.setCreateTime(now);
        registration.setUpdateTime(now);
        registration.setDeleted(0);
        registrationMapper.insert(registration);

        // 7. 返回报名 ID
        return registration.getId();
    }

    /**
     * 4. 取消报名(释放名额)
     */
    @Transactional
    @Override
    public void cancelRegister(Long userId, Long activityId) {
        // 1. 校验活动是否存在
        CommunityActivity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 2. 查询报名记录
        ActivityRegistration registration = registrationMapper.findByActivityIdAndUserId(activityId, userId);
        if (registration == null) {
            throw new BusinessException(404, "未报名该活动");
        }

        // 3. 只有未签到的才能取消
        if ("CHECKED_IN".equals(registration.getCheckInStatus())) {
            throw new BusinessException(400, "已签到无法取消报名");
        }

        // 4. 逻辑删除报名记录
        registrationMapper.cancel(userId, activityId);

        // 5. 释放名额
        activityMapper.decrementParticipants(activityId);
    }

    /**
     * 5. 查询我的活动
     */
    @Override
    public List<MyActivityVO> listMyActivities(Long userId) {
        List<ActivityRegistration> registrations = registrationMapper.findByUserId(userId);
        return registrations.stream().map(reg -> {
            CommunityActivity activity = activityMapper.findById(reg.getActivityId());
            if (activity == null) {
                return null;
            }
            return new MyActivityVO(
                    reg.getId(),
                    reg.getActivityId(),
                    activity.getTitle(),
                    activity.getCoverUrl(),
                    activity.getActivityStart(),
                    activity.getActivityEnd(),
                    activity.getStatus(),
                    reg.getCheckInStatus(),
                    reg.getCreateTime()
            );
        }).filter(v -> v != null).collect(Collectors.toList());
    }

    /**
     * 6. 活动签到(+50积分)
     */
    @Transactional
    @Override
    public void checkIn(Long userId, Long activityId) {
        // 1. 校验活动是否存在
        CommunityActivity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 2. 校验活动状态是否进行中
        if (!"IN_PROGRESS".equals(activity.getStatus())) {
            throw new BusinessException(400, "活动未进行中，无法签到");
        }

        // 3. 查询报名记录
        ActivityRegistration registration = registrationMapper.findByActivityIdAndUserId(activityId, userId);
        if (registration == null) {
            throw new BusinessException(400, "未报名该活动，无法签到");
        }

        // 4. 防重复签到
        if ("CHECKED_IN".equals(registration.getCheckInStatus())) {
            throw new BusinessException(400, "已签到，请勿重复签到");
        }

        // 5. 更新签到状态(原子操作)
        int rows = registrationMapper.updateCheckInStatus(
                registration.getId(), userId, "CHECKED_IN");
        if (rows == 0) {
            throw new BusinessException(500, "签到失败，请稍后重试");
        }

        // 6. 获取签到奖励积分(从 sys_config 读取)
        SysConfig config = sysConfigMapper.findByKey("checkin_bonus_points");
        Integer bonusPoints = config != null ? Integer.parseInt(config.getConfigValue()) : 50;

        // 7. 添加积分
        userService.addPoints(userId, bonusPoints, "ACTIVITY_CHECKIN", "活动签到奖励");
    }
}