package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.pojo.CommunityActivity;
import com.wmm.eldercare.core.vo.ActivityRegistrationVO;

import java.util.List;

/**
 * 管理端活动管理 Service
 */
public interface ActivityAdminService {

    /**
     * 分页查询活动列表（keyword 标题搜索，status 状态筛选，均可空）
     *
     * @param keyword  标题关键字（可空）
     * @param status   状态（DRAFT/REGISTRATING/IN_PROGRESS/ENDED，可空）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<CommunityActivity> listActivities(String keyword, String status, Integer pageNum, Integer pageSize);

    /**
     * 查询活动详情
     *
     * @param id 活动 ID
     * @return 活动详情
     */
    CommunityActivity getActivity(Long id);

    /**
     * 新增活动（默认状态 DRAFT，补全公共字段）
     *
     * @param activity 活动信息
     */
    void addActivity(CommunityActivity activity);

    /**
     * 修改活动
     *
     * @param id       活动 ID
     * @param activity 修改内容
     */
    void updateActivity(Long id, CommunityActivity activity);

    /**
     * 活动状态流转（DRAFT/REGISTRATING/IN_PROGRESS/ENDED）
     *
     * @param id     活动 ID
     * @param status 目标状态
     */
    void updateStatus(Long id, String status);

    /**
     * 逻辑删除活动
     *
     * @param id 活动 ID
     */
    void deleteActivity(Long id);

    /**
     * 查询活动报名名单（含手机号/姓名/签到状态）
     *
     * @param activityId 活动 ID
     * @return 报名名单
     */
    List<ActivityRegistrationVO> listRegistrations(Long activityId);
}