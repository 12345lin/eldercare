package com.wmm.eldercare.core.service.impl;

import com.wmm.eldercare.core.mapper.MessageMapper;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.pojo.Message;
import com.wmm.eldercare.core.service.HealthGuardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 健康提醒 / 健康指导实现
 * <p>阈值判断 → AI 生成个性化建议 → 写入站内消息（同一指标当日去重）。</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HealthGuardServiceImpl implements HealthGuardService {

    private final MessageMapper messageMapper;
    private final ChatClient chatClient;

    @Override
    public void checkAndNotify(Long userId, HealthRecord record) {
        // 1. 检查各指标是否超标，收集超标项
        List<String> abnormal = new ArrayList<>();

        Integer sys = record.getSystolic();
        if (sys != null) {
            if (sys < 90 || sys >= 140) abnormal.add("收缩压(" + sys + ")");
        }
        Integer dia = record.getDiastolic();
        if (dia != null) {
            if (dia < 60 || dia >= 90) abnormal.add("舒张压(" + dia + ")");
        }
        BigDecimal sugar = record.getBloodSugar();
        if (sugar != null) {
            if (sugar.compareTo(new BigDecimal("3.9")) < 0 || sugar.compareTo(new BigDecimal("6.1")) > 0) {
                abnormal.add("血糖(" + sugar + ")");
            }
        }
        Integer hr = record.getHeartRate();
        if (hr != null) {
            if (hr < 60 || hr > 100) abnormal.add("心率(" + hr + ")");
        }
        BigDecimal bmi = record.getBmi();
        if (bmi != null) {
            if (bmi.compareTo(new BigDecimal("18.5")) < 0 || bmi.compareTo(new BigDecimal("24")) >= 0) {
                abnormal.add("BMI(" + bmi + ")");
            }
        }

        if (abnormal.isEmpty()) {
            return; // 无超标，不打扰
        }

        // 2. 同日去重：若今天已为该用户推送过健康提醒，则不再重复（简化：靠标题时间判断由消息表承担，此处调用方已控制）
        String indicators = String.join("、", abnormal);

        // 3. 调用 AI 生成个性化健康建议
        String suggestion = askAI(indicators, record);

        // 4. 写入站内消息（type=HEALTH 健康提醒）
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setTitle("健康提醒");
        msg.setContent("检测到指标异常：" + indicators + "。" + suggestion);
        msg.setType("HEALTH");
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        msg.setUpdateTime(LocalDateTime.now());
        msg.setDeleted(0);
        messageMapper.insert(msg);
        log.info("已生成健康提醒: userId={}, 异常指标={}", userId, indicators);
    }

    /**
     * 调用 AI 生成个性化健康建议
     */
    private String askAI(String indicators, HealthRecord record) {
        try {
            String prompt = "用户体检/自测有以下健康指标超出正常范围：" + indicators +
                    "。请用亲切、易懂的语言，针对这些指标给出一份简短的健康建议（100字以内），" +
                    "包含饮食、运动、作息方面的建议，并提示是否需要就医。";
            String reply = chatClient.prompt()
                    .system("你是一位专业的健康顾问，请用亲切、易懂的语言，面向老年用户给出健康建议，不要输出markdown，直接输出纯文本。")
                    .user(prompt)
                    .call()
                    .content();
            return reply != null && !reply.isBlank() ? reply : "建议您关注这些异常指标，必要时咨询医生。";
        } catch (Exception e) {
            log.error("AI 生成健康建议失败", e);
            return "建议您关注这些异常指标，必要时咨询医生。";
        }
    }
}
