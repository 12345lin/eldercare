package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;
import com.wmm.eldercare.core.service.AppointmentAdminService;
import com.wmm.eldercare.core.vo.AppointmentAdminListVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端预约管理 Controller
 */
@RestController
@RequestMapping("/api/admin/appointments")
@Slf4j
@RequiredArgsConstructor
public class AppointmentAdminController {
    private final AppointmentAdminService appointmentAdminService;

    /**
     * 分页查询预约列表（可按状态筛选）
     * GET /api/admin/appointments?status=PENDING
     */
    @GetMapping
    public Result<PageResult<AppointmentAdminListVO>> listAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端查询预约列表: userId={}, status={}, pageNum={}", userId, status, pageNum);
        return Result.success(appointmentAdminService.listAppointments(status, pageNum, pageSize));
    }

    /**
     * 更新预约状态（确认/完成/取消）
     * PUT /api/admin/appointments/{id}/status?status=CONFIRMED
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateAppointmentStatus(@PathVariable Long id,
                                                @RequestParam String status,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端更新预约状态: userId={}, id={}, status={}", userId, id, status);
        appointmentAdminService.updateAppointmentStatus(id, status);
        return Result.success();
    }

    /**
     * 分页查询套餐列表
     * GET /api/admin/appointments/packages
     */
    @GetMapping("/packages")
    public Result<PageResult<AppointmentPackage>> listPackages(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端查询套餐列表: userId={}, pageNum={}", userId, pageNum);
        return Result.success(appointmentAdminService.listPackages(pageNum, pageSize));
    }

    /**
     * 新增套餐
     * POST /api/admin/appointments/packages
     */
    @PostMapping("/packages")
    public Result<Void> addPackage(@RequestBody AppointmentPackage appointmentPackage, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端新增套餐: userId={}, name={}", userId, appointmentPackage.getName());
        appointmentAdminService.addPackage(appointmentPackage);
        return Result.success();
    }

    /**
     * 修改套餐
     * PUT /api/admin/appointments/packages/{id}
     */
    @PutMapping("/packages/{id}")
    public Result<Void> updatePackage(@PathVariable Long id,
                                      @RequestBody AppointmentPackage appointmentPackage,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端修改套餐: userId={}, id={}", userId, id);
        appointmentAdminService.updatePackage(id, appointmentPackage);
        return Result.success();
    }

    /**
     * 删除套餐
     * DELETE /api/admin/appointments/packages/{id}
     */
    @DeleteMapping("/packages/{id}")
    public Result<Void> deletePackage(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端删除套餐: userId={}, id={}", userId, id);
        appointmentAdminService.deletePackage(id);
        return Result.success();
    }

    /**
     * 查询某套餐的时段列表
     * GET /api/admin/appointments/packages/{id}/slots
     */
    @GetMapping("/packages/{id}/slots")
    public Result<List<AppointmentSlot>> listSlots(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端查询套餐时段: userId={}, packageId={}", userId, id);
        return Result.success(appointmentAdminService.listSlots(id));
    }

    /**
     * 新增时段
     * POST /api/admin/appointments/slots
     */
    @PostMapping("/slots")
    public Result<Void> addSlot(@RequestBody AppointmentSlot slot, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端新增时段: userId={}, packageId={}, date={}", userId, slot.getPackageId(), slot.getAppointDate());
        appointmentAdminService.addSlot(slot);
        return Result.success();
    }

    /**
     * 删除时段
     * DELETE /api/admin/appointments/slots/{id}
     */
    @DeleteMapping("/slots/{id}")
    public Result<Void> deleteSlot(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("管理端删除时段: userId={}, id={}", userId, id);
        appointmentAdminService.deleteSlot(id);
        return Result.success();
    }
}