package com.wmm.eldercare.core.tool;

import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;
import com.wmm.eldercare.core.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 可调用的预约工具（Function Calling）
 *
 * <p>当用户在 AI 对话中提出"我要预约体检/帮我约明天体检"时，
 * AI 会调用这些工具完成：查套餐 → 查时段 → 下单预约。</p>
 *
 * <p>注意：单例 Bean 不能保存用户状态，因此 bookAppointment 需要显式传入 userId。</p>
 *
 * @author wmm
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentTools {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    private final AppointmentService appointmentService;

    /**
     * 供 ChatService 在每次调用 AI 前，绑定当前操作用户；调用后清理。
     */
    public static void bindUser(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static void clearUser() {
        CURRENT_USER.remove();
    }

    /**
     * 工具1：查询所有可预约的体检套餐
     */
    @Tool(description = "查询当前所有可预约的体检套餐，返回套餐名称、价格（积分）、适用人群和描述")
    public String searchPackages() {
        List<AppointmentPackage> list = appointmentService.listPackages();
        if (list.isEmpty()) {
            return "当前没有可预约的体检套餐。";
        }
        return list.stream()
                .map(p -> String.format(
                        "套餐ID=%d, 名称=%s, 价格=%d积分, 适合人群=%s, 描述=%s, 包含项目=%s",
                        p.getId(), p.getName(), p.getPrice(),
                        p.getSuitablePeople() == null ? "不限" : p.getSuitablePeople(),
                        p.getDescription() == null ? "" : p.getDescription(),
                        formatItems(p.getItems())
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 工具2：查询某套餐在某天的可预约时段
     */
    @Tool(description = "查询指定体检套餐在指定日期（YYYY-MM-DD格式）的可预约时间段，返回每个时段的ID、时间、剩余名额")
    public String listAvailableSlots(
            @ToolParam(description = "套餐ID，来自 searchPackages") Long packageId,
            @ToolParam(description = "预约日期，格式 YYYY-MM-DD，例如 2026-08-21") String date) {
        LocalDate d;
        try {
            d = LocalDate.parse(date);
        } catch (Exception e) {
            return "日期格式不正确，请使用 YYYY-MM-DD 格式，例如 2026-08-21。";
        }
        List<AppointmentSlot> slots;
        try {
            slots = appointmentService.listSlots(packageId, d);
        } catch (Exception e) {
            log.warn("查询时段失败 packageId={} date={}: {}", packageId, date, e.getMessage());
            return "查询时段失败：" + e.getMessage();
        }
        // 过滤可选（未满且未关闭）
        List<AppointmentSlot> available = slots.stream()
                .filter(s -> !"FULL".equals(s.getStatus()) && !"CLOSED".equals(s.getStatus()))
                .collect(Collectors.toList());
        if (available.isEmpty()) {
            return "该套餐当天没有可预约的时段了。";
        }
        return available.stream()
                .map(s -> String.format(
                        "时段ID=%d, 时间=%s, 剩余名额=%d",
                        s.getId(), s.getTimeRange(), s.getMaxCount() - s.getCurrentCount()
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 工具3：为当前用户下单预约某个时段
     */
    @Tool(description = "为当前登录用户预约指定时段的体检（会扣除对应积分），返回预约是否成功及预约ID")
    public String bookAppointment(
            @ToolParam(description = "要预约的时段ID，来自 listAvailableSlots") Long slotId) {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            return "未获取到当前用户，无法预约。请先登录。";
        }
        try {
            Long appointmentId = appointmentService.bookAppointment(userId, slotId);
            return "预约成功！预约ID为 " + appointmentId + "，状态为待确认。";
        } catch (Exception e) {
            log.warn("AI预约失败 userId={} slotId={}: {}", userId, slotId, e.getMessage());
            return "预约失败：" + e.getMessage();
        }
    }

    private String formatItems(String items) {
        if (items == null || items.isBlank()) return "无";
        try {
            List<String> arr = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(items, List.class);
            return String.join("、", arr);
        } catch (Exception ignored) {
            return items;
        }
    }
}
