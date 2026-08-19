package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.AssessmentResult;
import com.wmm.eldercare.core.vo.AssessmentAdminResultVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssessmentResultMapper {

    /**
     * 插入评测结果
     */
    int insert(AssessmentResult result);

    /**
     * 管理端分页查询评测结果列表（JOIN user 显示手机号/姓名、JOIN 问卷显示标题）
     */
    List<AssessmentAdminResultVO> findAllAdmin();

    /**
     * 根据 ID 查询评测结果
     */
    AssessmentResult findById(@Param("id") Long id);

    /**
     * 查询某用户的所有评测记录（按创建时间倒序）
     */
    List<AssessmentResult> findByUserId(@Param("userId") Long userId);

    /**
     * 统计某用户评测总次数（个人中心统计面板）
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 逻辑删除评测结果（防越权：只能删自己的）
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
