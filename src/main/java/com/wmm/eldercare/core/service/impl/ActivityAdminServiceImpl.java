package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.CommunityActivityMapper;
import com.wmm.eldercare.core.pojo.CommunityActivity;
import com.wmm.eldercare.core.service.ActivityAdminService;
import com.wmm.eldercare.core.vo.ActivityRegistrationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityAdminServiceImpl implements ActivityAdminService {

    private final CommunityActivityMapper activityMapper;

    /**
     * 分页查询活动列表（keyword 标题搜索，status 状态筛选，均可空）
     * @param keyword  标题关键字（可空）
     * @param status   状态（DRAFT/REGISTRATING/IN_PROGRESS/ENDED，可空）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public PageResult<CommunityActivity> listActivities(String keyword, String status, Integer pageNum, Integer pageSize) {
        //1.开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        //2.封装分页结果
        List<CommunityActivity> activities = activityMapper.findAll(keyword, status);
        PageInfo<CommunityActivity> pageInfo = new PageInfo<>(activities);
        //3.返回分页结果
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageNum,
                pageSize,
                pageInfo.getPages()
        );

    }

    /**
     * 查询活动详情
     * @param id 活动 ID
     * @return
     */
    @Override
    public CommunityActivity getActivity(Long id) {
        //1.根据 ID 查询活动
        CommunityActivity activity = activityMapper.findById(id);
        //2.判断活动是否存在
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        //3.返回活动详情
        return activity;
    }

    /**
     * 新增活动
     * @param activity 活动信息
     */
    @Override
    public void addActivity(CommunityActivity activity) {
        //1.设置默认状态为 DRAFT
        activity.setStatus("DRAFT");
        //2.补全公共字段
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        activity.setDeleted(0);
        //3.新增活动
        activityMapper.insert(activity);
    }

    /**
     * 修改活动
     * @param id       活动 ID
     * @param activity 修改内容
     */
    @Override
    public void updateActivity(Long id, CommunityActivity activity) {
        //1.根据 ID 查询活动
        CommunityActivity existingActivity = activityMapper.findById(id);
        //2.判断活动是否存在
        if (existingActivity == null) {
            throw new BusinessException(400, "活动不存在");
        }
        //3.更新活动信息
        activityMapper.update(id, activity);
           }

    /**
     * 更新活动状态
     * @param id     活动 ID
     * @param status 目标状态
     */
    @Override
    public void updateStatus(Long id, String status) {
        //1.根据 ID 查询活动
        CommunityActivity existingActivity = activityMapper.findById(id);
        //2.判断活动是否存在
        if (existingActivity == null) {
            throw new BusinessException(400, "活动不存在");
        }
        //3.更新活动状态
        activityMapper.updateStatus(id, status);
    }

    /**
     * 删除活动
     * @param id 活动 ID
     */
    @Override
    public void deleteActivity(Long id) {
        //1.根据 ID 查询活动
        CommunityActivity existingActivity = activityMapper.findById(id);
        //2.判断活动是否存在
        if (existingActivity == null) {
            throw new BusinessException(400, "活动不存在");
        }
        //3.逻辑删除活动
        activityMapper.deleteById(id);
    }

    /**
     * 查询某活动报名名单（JOIN user 显示手机号/姓名）
     * @param activityId 活动 ID
     * @return
     */
    @Override
    public List<ActivityRegistrationVO> listRegistrations(Long activityId) {
        //1.获得活动信息
        CommunityActivity activity = activityMapper.findById(activityId);
        //2.判断活动是否存在
        if (activity == null) {
            throw new BusinessException(400, "活动不存在");
        }
        //3.查询报名记录
        return activityMapper.findRegistrations(activityId);
    }
}
