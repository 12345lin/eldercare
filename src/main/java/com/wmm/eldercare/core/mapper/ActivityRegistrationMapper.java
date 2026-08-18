package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityRegistrationMapper {

    /**
     * 插入报名记录
     */
    int insert(ActivityRegistration registration);

    /**
     * 查询用户对某活动的报名记录(判断是否已报名)
     */
    ActivityRegistration findByActivityIdAndUserId(@Param("activityId") Long activityId,
                                                   @Param("userId") Long userId);

    /**
     * 查询用户的所有报名记录(我的活动)
     */
    List<ActivityRegistration> findByUserId(@Param("userId") Long userId);

    /**
     * 统计某用户报名活动总数（个人中心统计面板）
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 签到:只有未签到状态才更新,防止重复签到
     *
     * @return 影响行数,0 表示已签过或记录不存在
     */
    int updateCheckInStatus(@Param("id") Long id,
                            @Param("userId") Long userId,
                            @Param("checkInStatus") String checkInStatus);

    /**
     * 取消报名(逻辑删除)
     */
    int cancel(@Param("userId") Long userId,
               @Param("activityId") Long activityId);
}