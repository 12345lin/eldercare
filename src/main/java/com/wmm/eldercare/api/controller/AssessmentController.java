package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.dto.AssessmentSubmitDTO;
import com.wmm.eldercare.core.service.AssessmentService;
import com.wmm.eldercare.core.vo.AssessmentResultVO;
import com.wmm.eldercare.core.vo.QuestionnaireDetailVO;
import com.wmm.eldercare.core.vo.QuestionnaireListVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
@Slf4j
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    /**
     * 分页查询已发布问卷列表
     */
    @GetMapping
    public Result<PageResult<QuestionnaireListVO>> listPublished(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询已发布问卷列表: pageNum={}, pageSize={}", pageNum, pageSize);
        PageResult<QuestionnaireListVO> pageResult = assessmentService.listPublished(pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 获取问卷详情（含题目列表）
     */
    @GetMapping("/{id}")
    public Result<QuestionnaireDetailVO> getDetail(@PathVariable Long id) {
        log.info("获取问卷详情: id={}", id);
        return Result.success(assessmentService.getDetail(id));
    }

    /**
     * 提交答卷
     */
    @PostMapping("/submit")
    public Result<Long> submitAssessment(@RequestBody AssessmentSubmitDTO dto,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("提交答卷: userId={}, questionnaireId={}", userId, dto.getQuestionnaireId());
        Long resultId = assessmentService.submitAssessment(userId, dto);
        return Result.success(resultId);
    }

    /**
     * 查询我的评测历史
     */
    @GetMapping("/my")
    public Result<PageResult<AssessmentResultVO>> listMyAssessments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我的评测历史: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);
        return Result.success(assessmentService.listResults(userId, pageNum, pageSize));
    }
}
