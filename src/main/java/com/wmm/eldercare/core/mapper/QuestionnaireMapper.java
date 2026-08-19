package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.Questionnaire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionnaireMapper {

    /**
     * 查询所有已发布的问卷（按创建时间倒序）
     */
    List<Questionnaire> listPublished();

    /**
     * 根据 ID 查询问卷详情
     */
    Questionnaire findById(@Param("id") Long id);

    /**
     * 插入问卷
     */
    int insert(Questionnaire questionnaire);

    /**
     * 更新问卷状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 逻辑删除问卷
     */
    int deleteById(@Param("id") Long id);

    /**
     * 管理端分页查询问卷列表（keyword 标题搜索，status 状态筛选，均可空）
     */
    List<Questionnaire> findAll(@Param("keyword") String keyword, @Param("status") String status);

    /**
     * 管理端修改问卷（动态 SQL）
     */
    int update(@Param("id") Long id, @Param("questionnaire") Questionnaire questionnaire);
}
