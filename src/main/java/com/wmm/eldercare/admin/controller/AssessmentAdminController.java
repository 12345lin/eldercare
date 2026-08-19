package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.Questionnaire;
import com.wmm.eldercare.core.service.AssessmentAdminService;
import com.wmm.eldercare.core.vo.AssessmentAdminResultVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端评测管理 Controller
 */
@RestController
@RequestMapping("/api/admin/assessments")
@Slf4j
@RequiredArgsConstructor
public class AssessmentAdminController {
    private final AssessmentAdminService assessmentAdminService;

    /**
     * 分页查询问卷列表
     * GET /api/admin/assessments?keyword=x&status=PUBLISHED
     */
    @GetMapping
    public Result<PageResult<Questionnaire>> listQuestionnaires(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端查询问卷列表: userId={}, keyword={}, status={}", userId, keyword, status);
        return Result.success(assessmentAdminService.listQuestionnaires(keyword, status, pageNum, pageSize));
    }

    /**
     * 查询问卷详情
     * GET /api/admin/assessments/{id}
     */
    @GetMapping("/{id}")
    public Result<Questionnaire> getQuestionnaire(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端查询问卷详情: userId={}, id={}", userId, id);
        return Result.success(assessmentAdminService.getQuestionnaire(id));
    }

    /**
     * 新增问卷
     * POST /api/admin/assessments
     */
    @PostMapping
    public Result<Void> addQuestionnaire(@RequestBody Questionnaire questionnaire, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端新增问卷: userId={}, title={}", userId, questionnaire.getTitle());
        assessmentAdminService.addQuestionnaire(questionnaire);
        return Result.success();
    }

    /**
     * 修改问卷
     * PUT /api/admin/assessments/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateQuestionnaire(@PathVariable Long id,
                                            @RequestBody Questionnaire questionnaire,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端修改问卷: userId={}, id={}", userId, id);
        assessmentAdminService.updateQuestionnaire(id, questionnaire);
        return Result.success();
    }

    /**
     * 发布问卷
     * PUT /api/admin/assessments/{id}/publish
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishQuestionnaire(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端发布问卷: userId={}, id={}", userId, id);
        assessmentAdminService.publishQuestionnaire(id);
        return Result.success();
    }

    /**
     * 删除问卷
     * DELETE /api/admin/assessments/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestionnaire(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端删除问卷: userId={}, id={}", userId, id);
        assessmentAdminService.deleteQuestionnaire(id);
        return Result.success();
    }

    /**
     * 分页查询评测结果列表
     * GET /api/admin/assessments/results?pageNum=1&pageSize=10
     */
    @GetMapping("/results")
    public Result<PageResult<AssessmentAdminResultVO>> listResults(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端查询评测结果: userId={}, pageNum={}", userId, pageNum);
        return Result.success(assessmentAdminService.listResults(pageNum, pageSize));
    }
}