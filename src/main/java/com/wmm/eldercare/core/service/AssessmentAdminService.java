package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.pojo.Questionnaire;
import com.wmm.eldercare.core.vo.AssessmentAdminResultVO;

/**
 * 管理端评测管理 Service
 */
public interface AssessmentAdminService {

    /**
     * 分页查询问卷列表（keyword 标题搜索，status 状态筛选，均可空）
     */
    PageResult<Questionnaire> listQuestionnaires(String keyword, String status, Integer pageNum, Integer pageSize);

    /**
     * 查询问卷详情
     */
    Questionnaire getQuestionnaire(Long id);

    /**
     * 新增问卷（默认状态 DRAFT）
     */
    void addQuestionnaire(Questionnaire questionnaire);

    /**
     * 修改问卷
     */
    void updateQuestionnaire(Long id, Questionnaire questionnaire);

    /**
     * 发布问卷（DRAFT → PUBLISHED）
     */
    void publishQuestionnaire(Long id);

    /**
     * 逻辑删除问卷
     */
    void deleteQuestionnaire(Long id);

    /**
     * 查询某问卷下的所有题目
     */
    java.util.List<com.wmm.eldercare.core.pojo.Question> listQuestions(Long questionnaireId);

    /**
     * 新增题目
     */
    void addQuestion(Long questionnaireId, com.wmm.eldercare.core.pojo.Question question);

    /**
     * 修改题目
     */
    void updateQuestion(Long id, com.wmm.eldercare.core.pojo.Question question);

    /**
     * 删除题目
     */
    void deleteQuestion(Long id);

    /**
     * 分页查询评测结果列表（含用户/问卷信息）
     */
    PageResult<AssessmentAdminResultVO> listResults(Integer pageNum, Integer pageSize);
}