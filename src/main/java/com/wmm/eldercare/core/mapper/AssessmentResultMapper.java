package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.AssessmentResult;
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
     * 根据 ID 查询评测结果
     */
    AssessmentResult findById(@Param("id") Long id);

    /**
     * 查询某用户的所有评测记录（按创建时间倒序）
     */
    List<AssessmentResult> findByUserId(@Param("userId") Long userId);

    /**
     * 逻辑删除评测结果（防越权：只能删自己的）
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
