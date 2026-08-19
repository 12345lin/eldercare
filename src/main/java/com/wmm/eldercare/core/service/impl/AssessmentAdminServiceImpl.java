package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.AssessmentResultMapper;
import com.wmm.eldercare.core.mapper.QuestionnaireMapper;
import com.wmm.eldercare.core.pojo.Questionnaire;
import com.wmm.eldercare.core.service.AssessmentAdminService;
import com.wmm.eldercare.core.vo.AssessmentAdminResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentAdminServiceImpl implements AssessmentAdminService {

    private final QuestionnaireMapper questionnaireMapper;
    private final AssessmentResultMapper assessmentResultMapper;

    @Override
    public PageResult<Questionnaire> listQuestionnaires(String keyword, String status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Questionnaire> list = questionnaireMapper.findAll(keyword, status);
        PageInfo<Questionnaire> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    @Override
    public Questionnaire getQuestionnaire(Long id) {
        Questionnaire questionnaire = questionnaireMapper.findById(id);
        if (questionnaire == null) {
            throw new BusinessException(404, "问卷不存在");
        }
        return questionnaire;
    }

    @Override
    public void addQuestionnaire(Questionnaire questionnaire) {
        if (questionnaire.getTitle() == null || questionnaire.getTitle().isBlank()) {
            throw new BusinessException(400, "问卷标题不能为空");
        }
        questionnaire.setStatus("DRAFT");
        questionnaire.setCreateTime(LocalDateTime.now());
        questionnaire.setUpdateTime(LocalDateTime.now());
        questionnaire.setDeleted(0);
        questionnaireMapper.insert(questionnaire);
    }

    @Override
    public void updateQuestionnaire(Long id, Questionnaire questionnaire) {
        if (questionnaireMapper.findById(id) == null) {
            throw new BusinessException(404, "问卷不存在");
        }
        questionnaireMapper.update(id, questionnaire);
    }

    @Override
    public void publishQuestionnaire(Long id) {
        if (questionnaireMapper.findById(id) == null) {
            throw new BusinessException(404, "问卷不存在");
        }
        questionnaireMapper.updateStatus(id, "PUBLISHED");
    }

    @Override
    public void deleteQuestionnaire(Long id) {
        if (questionnaireMapper.findById(id) == null) {
            throw new BusinessException(404, "问卷不存在");
        }
        questionnaireMapper.deleteById(id);
    }

    @Override
    public PageResult<AssessmentAdminResultVO> listResults(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AssessmentAdminResultVO> list = assessmentResultMapper.findAllAdmin();
        PageInfo<AssessmentAdminResultVO> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }
}