package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.vo.ActivityDetailVO;
import com.wmm.eldercare.core.vo.ActivityListVO;
import com.wmm.eldercare.core.vo.MyActivityVO;

import java.util.List;

public interface ActivityService {

    /**
     * 查询可报名活动列表(报名中/进行中,带"我是否已报名"标记)
     *
     * @param userId 当前用户 ID
     * @return 活动列表 VO
     */
    List<ActivityListVO> listAvailable(Long userId);

    /**
     * 查询活动详情(带报名状态和签到状态)
     *
     * @param userId     当前用户 ID
     * @param activityId 活动 ID
     * @return 活动详情 VO
     */
    ActivityDetailVO getDetail(Long userId, Long activityId);

    /**
     * 报名活动(并发名额控制,一人只能报一次)
     *
     * @param userId     当前用户 ID
     * @param activityId 活动 ID
     * @return 报名记录 ID
     */
    Long register(Long userId, Long activityId);

    /**
     * 取消报名(释放名额)
     *
     * @param userId     当前用户 ID
     * @param activityId 活动 ID
     */
    void cancelRegister(Long userId, Long activityId);

    /**
     * 查询我的报名记录(带活动信息和签到状态)
     *
     * @param userId 当前用户 ID
     * @return 我的活动 VO 列表
     */
    List<MyActivityVO> listMyActivities(Long userId);

    /**
     * 活动签到(校验已报名+活动进行中,签到赠送积分)
     *
     * @param userId     当前用户 ID
     * @param activityId 活动 ID
     */
    void checkIn(Long userId, Long activityId);
}