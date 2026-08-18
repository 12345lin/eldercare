package com.wmm.eldercare.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.dto.AssessmentSubmitDTO;
import com.wmm.eldercare.core.mapper.AssessmentResultMapper;
import com.wmm.eldercare.core.mapper.QuestionMapper;
import com.wmm.eldercare.core.mapper.QuestionnaireMapper;
import com.wmm.eldercare.core.mapper.SysConfigMapper;
import com.wmm.eldercare.core.pojo.AssessmentResult;
import com.wmm.eldercare.core.pojo.Question;
import com.wmm.eldercare.core.pojo.Questionnaire;
import com.wmm.eldercare.core.pojo.SysConfig;
import com.wmm.eldercare.core.service.AssessmentService;
import com.wmm.eldercare.core.service.UserService;
import com.wmm.eldercare.core.vo.AssessmentResultVO;
import com.wmm.eldercare.core.vo.QuestionDetailVO;
import com.wmm.eldercare.core.vo.QuestionnaireDetailVO;
import com.wmm.eldercare.core.vo.QuestionnaireListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {
    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionMapper questionMapper;
    private final AssessmentResultMapper assessmentResultMapper;
    private final UserService userService;
    private final SysConfigMapper sysConfigMapper;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<QuestionnaireListVO> listPublished(Integer pageNum, Integer pageSize) {
        //1.开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        //2.查询已发布问卷
        List<Questionnaire> list = questionnaireMapper.listPublished();
        if (list.isEmpty()) {
            throw new BusinessException(404, "暂无已发布问卷");
        }
        //3.转换实体 → VO
        List<QuestionnaireListVO> voList = list.stream().map(q -> new QuestionnaireListVO(
                q.getId(), q.getTitle(), q.getDescription(), q.getCreateTime().toString()
        )).collect(Collectors.toList());

        //4.封装返回结果
        PageInfo<Questionnaire> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                voList,
                pageNum,
                pageSize,
                pageInfo.getPages()
        );
    }

    @Override
    public QuestionnaireDetailVO getDetail(Long id) {
        //1. 根据 ID 查询问卷
        Questionnaire questionnaire = questionnaireMapper.findById(id);
        if (questionnaire == null) {
            throw new BusinessException(404, "问卷不存在");
        }

        QuestionnaireDetailVO vo = new QuestionnaireDetailVO();
        vo.setId(questionnaire.getId());
        vo.setTitle(questionnaire.getTitle());
        vo.setDescription(questionnaire.getDescription());

        //2. 查询题目列表
        List<Question> questionList = questionMapper.findByQuestionnaireId(id);

        //3. 转换：实体类 → VO
        List<QuestionDetailVO> questionVOs = questionList.stream()
                .map(q -> new QuestionDetailVO(
                        q.getId(),
                        q.getContent(),
                        q.getType(),
                        q.getOptions(),
                        q.getSortOrder()
                ))
                .collect(Collectors.toList());
        vo.setQuestions(questionVOs);
        return vo;
    }

    @Override
    @Transactional
    public Long submitAssessment(Long userId, AssessmentSubmitDTO dto) {
        // 1. 校验问卷
        Questionnaire questionnaire = questionnaireMapper.findById(dto.getQuestionnaireId());
        if (questionnaire == null || !"PUBLISHED".equals(questionnaire.getStatus())) {
            throw new BusinessException(400, "问卷不存在或未发布");
        }

        // 2. 查题目并校验答案
        List<Question> questions = questionMapper.findByQuestionnaireId(dto.getQuestionnaireId());
        Map<Long, String> answerMap = dto.getAnswers().stream()
                .collect(Collectors.toMap(AssessmentSubmitDTO.AnswerItemDTO::getQuestionId, AssessmentSubmitDTO.AnswerItemDTO::getAnswer));

        for (Question q : questions) {
            if (!answerMap.containsKey(q.getId())) {
                throw new BusinessException(400, "请完成所有题目");
            }
        }

        // 3. 转换答案 JSON
        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(dto.getAnswers());
        } catch (Exception e) {
            throw new BusinessException(500, "答案序列化失败");
        }

        // 4. 调用 AI 评分和建议
        String aiResponse = callAIForAssessment(questions, answerMap, questionnaire.getTitle());

        // 解析 AI 返回的 JSON（格式：{"score": 80, "suggestion": "..."}）
        // Agnes 可能返回 markdown 代码块包裹，需要先提取 JSON 片段再解析
        int aiScore = 0;
        String aiSuggestion = "";
        try {
            JsonNode jsonNode = extractJsonFromAiResponse(aiResponse);
            if (jsonNode != null && jsonNode.has("score")) {
                aiScore = jsonNode.get("score").asInt(0);
                aiSuggestion = jsonNode.get("suggestion").asText("");
            } else {
                aiSuggestion = "AI 评分解析失败，请稍后重试";
            }
        } catch (Exception e) {
            // AI 返回格式异常，使用提示文案
            aiSuggestion = "AI 评分解析失败，请稍后重试";
        }

        // 5. 保存评测结果
        AssessmentResult result = new AssessmentResult();
        result.setUserId(userId);
        result.setQuestionnaireId(dto.getQuestionnaireId());
        result.setAnswers(answersJson);
        result.setAiScore(aiScore);
        result.setAiSuggestion(aiSuggestion);
        result.setCreateTime(LocalDateTime.now());
        result.setUpdateTime(LocalDateTime.now());
        result.setDeleted(0);

        assessmentResultMapper.insert(result);
        Long resultId = result.getId();

        // 6. 加 20 积分
        userService.addPoints(userId, 20, "HEALTH_ASSESSMENT", "完成健康评测");

        return resultId;
    }

    /**
     * 从 AI 返回文本中提取 JSON 对象
     *
     * <p>Agnes 的返回可能被 markdown 代码块（```json ... ```）包裹，
     * 或前后带有解释性文字，直接 readTree 会解析失败。
     * 做法：去掉代码块标记后，截取第一个 {@code {} 到最后一个 {@code }} 之间的内容再解析。</p>
     */
    private JsonNode extractJsonFromAiResponse(String response) throws Exception {
        // 1. 去掉 markdown 代码块标记（```json 或 ```）
        String text = response.trim().replaceAll("^```(?:json)?\\s*", "").replaceAll("\\s*```$", "");
        // 2. 提取第一个 { 到最后一个 } 之间的内容
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        return objectMapper.readTree(text.substring(start, end + 1));
    }

    /**
     * 调用 AI 进行健康评测评分
     */
    private String callAIForAssessment(List<Question> questions, Map<Long, String> answerMap, String questionnaireTitle) {
        // 构建评测问题描述
        StringBuilder assessmentPrompt = new StringBuilder();
        assessmentPrompt.append("请根据以下健康问卷答案进行评分和建议。\n\n");
        assessmentPrompt.append("问卷名称：").append(questionnaireTitle).append("\n\n");
        assessmentPrompt.append("题目和答案：\n");

        for (Question q : questions) {
            String answer = answerMap.getOrDefault(q.getId(), "未回答");
            assessmentPrompt.append("- ").append(q.getContent()).append("\n");
            assessmentPrompt.append("  答案：").append(answer).append("\n\n");
        }

        assessmentPrompt.append("\n请以JSON格式返回评分和建议，格式如下：\n");
        assessmentPrompt.append("{\"score\": 分数(0-100), \"suggestion\": \"健康建议文本\"}\n");
        assessmentPrompt.append("请只输出JSON，不要使用markdown代码块或其他说明文字。\n");

        // 从数据库获取系统提示词
        SysConfig config = sysConfigMapper.findByKey("assessment_ai_prompt");
        String systemPrompt = config != null ? config.getConfigValue()
                               : "你是一位专业的健康顾问，请根据用户的健康问卷答案进行评分，分数越高表示健康状况越好。";

        // 调用 AI
        String response = chatClient.prompt()
            .system(systemPrompt)
            .user(assessmentPrompt.toString())
            .call()
            .content();

        return response;
    }

    @Override
    public PageResult<AssessmentResultVO> listResults(Long userId, Integer pageNum, Integer pageSize) {
        // 1. 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 2. 查询数据（Mapper 不分页，只查数据）
        List<AssessmentResult> list = assessmentResultMapper.findByUserId(userId);

        // 3. 封装分页结果
        PageInfo<AssessmentResult> pageInfo = new PageInfo<>(list);

        // 4. 转换 VO
        List<AssessmentResultVO> voList = list.stream().map(result -> {
            AssessmentResultVO vo = new AssessmentResultVO();
            vo.setId(result.getId());
            vo.setQuestionnaireId(result.getQuestionnaireId());
            vo.setAiScore(result.getAiScore());
            vo.setAiSuggestion(result.getAiSuggestion());
            vo.setCreateTime(result.getCreateTime().toString());

            // 查问卷标题
            Questionnaire questionnaire = questionnaireMapper.findById(result.getQuestionnaireId());
            if (questionnaire != null) {
                vo.setQuestionnaireTitle(questionnaire.getTitle());
            }

            return vo;
        }).collect(Collectors.toList());

        // 5. 返回分页结果
        return new PageResult<>(
                pageInfo.getTotal(),
                voList,
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }
}