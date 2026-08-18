package com.wmm.eldercare.api.controller;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.dto.AppointmentBookDTO;
import com.wmm.eldercare.core.mapper.AppointmentPackageMapper;
import com.wmm.eldercare.core.mapper.AppointmentSlotMapper;
import com.wmm.eldercare.core.pojo.Appointment;
import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;
import com.wmm.eldercare.core.service.AppointmentService;
import com.wmm.eldercare.core.vo.AppointmentDetailVO;
import com.wmm.eldercare.core.vo.AppointmentPackageVO;
import com.wmm.eldercare.core.vo.AppointmentSlotVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@Slf4j
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentPackageMapper packageMapper;
    private final AppointmentSlotMapper slotMapper;

    /**
     * 1. 查询所有可用体检套餐
     * GET /api/appointments/packages
     */
    @GetMapping("/packages")
    public Result<List<AppointmentPackageVO>> listPackages() {
        log.info("查询体检套餐列表");
        List<AppointmentPackage> packages = appointmentService.listPackages();
        return Result.success(packages.stream()
            .map(p -> new AppointmentPackageVO(
                p.getId(), p.getName(), p.getCoverUrl(), p.getDescription(),
                p.getPrice(), p.getSuitablePeople(), p.getItems(),
                p.getStatus(), p.getCreateTime()
            ))
            .collect(Collectors.toList()));
    }

    /**
     * 2. 查询某套餐在某日期的可预约时段
     * GET /api/appointments/slots?packageId=1&date=2026-08-20
     */
    @GetMapping("/slots")
    public Result<List<AppointmentSlotVO>> listSlots(
            @RequestParam Long packageId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("查询预约时段: packageId={}, date={}", packageId, date);
        List<AppointmentSlot> slots = appointmentService.listSlots(packageId, date);
        return Result.success(slots.stream()
            .map(s -> new AppointmentSlotVO(
                s.getId(), s.getPackageId(), s.getAppointDate(),
                s.getTimeRange(), s.getMaxCount(), s.getCurrentCount(),
                s.getStatus(), s.getCreateTime()
            ))
            .collect(Collectors.toList()));
    }

    /**
     * 3. 预约体检
     * POST /api/appointments/book
     */
    @PostMapping("/book")
    public Result<Long> bookAppointment(
            @RequestBody AppointmentBookDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("预约体检: userId={}, slotId={}", userId, dto.getSlotId());
        Long appointmentId = appointmentService.bookAppointment(userId, dto.getSlotId());
        return Result.success(appointmentId);
    }

    /**
     * 4. 取消预约
     * POST /api/appointments/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelAppointment(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("取消预约: userId={}, appointmentId={}", userId, id);
        appointmentService.cancelAppointment(userId, id);
        return Result.success();
    }

    /**
     * 5. 查询我的预约记录
     * GET /api/appointments/my
     */
    @GetMapping("/my")
    public Result<List<AppointmentDetailVO>> listMyAppointments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我的预约: userId={}", userId);
        List<Appointment> appointments = appointmentService.listMyAppointments(userId);
        
        return Result.success(appointments.stream().map(a -> {
            AppointmentPackage pkg = packageMapper.findById(a.getPackageId());
            AppointmentSlot slot = slotMapper.findById(a.getSlotId());
            return new AppointmentDetailVO(
                a.getId(),
                a.getSlotId(),
                a.getPackageId(),
                pkg != null ? pkg.getName() : "",
                slot != null ? slot.getTimeRange() : "",
                slot != null ? slot.getAppointDate().toString() : "",
                a.getStatus(),
                a.getReportUrl(),
                a.getCreateTime()
            );
        }).collect(Collectors.toList()));
    }
}
