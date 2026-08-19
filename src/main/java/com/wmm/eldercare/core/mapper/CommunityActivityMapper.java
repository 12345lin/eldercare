package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.CommunityActivity;
import com.wmm.eldercare.core.vo.ActivityRegistrationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityActivityMapper {

    /**
     * 查询会员可见的活动列表(报名中/进行中,不含草稿和已结束)
     */
    List<CommunityActivity> listAvailable();

    /**
     * 根据 ID 查询活动
     */
    CommunityActivity findById(@Param("id") Long id);

    /**
     * 管理端新增活动
     */
    int insert(CommunityActivity activity);

    /**
     * 管理端分页查询活动列表（keyword 按标题模糊搜索，status 状态筛选，均可空）
     */
    List<CommunityActivity> findAll(@Param("keyword") String keyword, @Param("status") String status);

    /**
     * 管理端修改活动（动态 SQL）
     */
    int update(@Param("id") Long id, @Param("activity") CommunityActivity activity);

    /**
     * 管理端活动状态流转（DRAFT/REGISTRATING/IN_PROGRESS/ENDED）
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 管理端逻辑删除活动
     */
    int deleteById(@Param("id") Long id);
    /**
     * 查询某活动报名名单（JOIN user 显示手机号/姓名）
     */
    List<ActivityRegistrationVO> findRegistrations(@Param("activityId") Long activityId);

    /**
     * 原子增加报名人数(并发控制:current_participants < max_participants 才成功)
     *
     * @return 影响行数,0 表示名额已满
     */
    int incrementParticipants(@Param("id") Long id);

    /**
     * 原子减少报名人数(取消报名时释放名额)
     */
    int decrementParticipants(@Param("id") Long id);
}