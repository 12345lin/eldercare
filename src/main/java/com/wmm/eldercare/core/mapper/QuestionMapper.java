package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper {

    /**
     * 根据问卷 ID 查询所有题目（按排序号升序）
     */
    List<Question> findByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    /**
     * 批量根据 ID 查询题目
     */
    List<Question> findByIdIn(@Param("ids") List<Long> ids);

    /**
     * 插入题目
     */
    int insert(Question question);

    /**
     * 更新题目
     */
    int update(Question question);

    /**
     * 逻辑删除题目
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据问卷 ID 逻辑删除所有题目
     */
    int deleteByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);
}
