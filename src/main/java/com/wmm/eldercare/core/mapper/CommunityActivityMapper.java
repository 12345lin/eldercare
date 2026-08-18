package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.CommunityActivity;
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