package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.dto.AssessmentSubmitDTO;
import com.wmm.eldercare.core.vo.AssessmentResultVO;
import com.wmm.eldercare.core.vo.QuestionnaireDetailVO;
import com.wmm.eldercare.core.vo.QuestionnaireListVO;

public interface AssessmentService {

    /**
     * 分页查询已发布问卷列表
     */
    PageResult<QuestionnaireListVO> listPublished(Integer pageNum, Integer pageSize);

    /**
     * 获取问卷详情（含题目列表）
     */
    QuestionnaireDetailVO getDetail(Long id);

    /**
     * 提交答卷
     */
    Long submitAssessment(Long userId, AssessmentSubmitDTO dto);

    /**
     * 查询我的评测历史
     */
    PageResult<AssessmentResultVO> listResults(Long userId, Integer pageNum, Integer pageSize);
}
