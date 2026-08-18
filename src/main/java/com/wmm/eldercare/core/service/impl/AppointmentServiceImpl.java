package com.wmm.eldercare.core.service.impl;

import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.mapper.AppointmentMapper;
import com.wmm.eldercare.core.mapper.AppointmentPackageMapper;
import com.wmm.eldercare.core.mapper.AppointmentSlotMapper;
import com.wmm.eldercare.core.pojo.Appointment;
import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;
import com.wmm.eldercare.core.service.AppointmentService;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentSlotMapper slotMapper;
    private final AppointmentPackageMapper packageMapper;
    private final UserService userService;

    /**
     * 查询所有可用套餐
     */
    @Override
    public List<AppointmentPackage> listPackages() {
        return packageMapper.listEnabled();
    }

    /**
     * 查询指定套餐在指定日期的可用时间槽
     */
    @Override
    public List<AppointmentSlot> listSlots(Long packageId, LocalDate date) {
        // 1. 校验套餐是否存在
        AppointmentPackage packageEntity = packageMapper.findById(packageId);
        if (packageEntity == null) {
            throw new BusinessException(404, "套餐不存在");
        }
        // 2. 查询该套餐在该日期的时段
        return slotMapper.findByPackageIdAndDate(packageId, date);
    }

    /**
     * 预约体检
     */
    @Transactional
    @Override
    public Long bookAppointment(Long userId, Long slotId) {
        // 1. 校验时段是否存在
        AppointmentSlot slotEntity = slotMapper.findById(slotId);
        if (slotEntity == null) {
            throw new BusinessException(404, "时段不存在");
        }
        // 2. 校验时段是否已满
        if (slotEntity.getCurrentCount() >= slotEntity.getMaxCount()) {
            throw new BusinessException(400, "时段已满，预约失败");
        }
        // 3. 校验预约是否已关闭
        if ("CLOSED".equals(slotEntity.getStatus())) {
            throw new BusinessException(400, "预约已关闭");
        }
        // 4. 检查用户积分是否足够
        Integer points = userService.getPoints(userId);
        AppointmentPackage packageEntity = packageMapper.findById(slotEntity.getPackageId());
        if (packageEntity == null) {
            throw new BusinessException(404, "套餐不存在");
        }
        if (points < packageEntity.getPrice()) {
            throw new BusinessException(400, "积分不足，无法预约");
        }
        // 5. 并发控制：增加时段预约人数
        int rows = slotMapper.incrementCount(slotId);
        if (rows == 0) {
            throw new BusinessException(400, "名额已满，预约失败");
        }
        // 6. 扣除用户积分
        userService.deductPoints(userId, packageEntity.getPrice(), "APPOINTMENT",
                "预约体检套餐：" + packageEntity.getName());
        // 7. 创建预约记录
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setSlotId(slotId);
        appointment.setPackageId(slotEntity.getPackageId());
        appointment.setStatus("PENDING");
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointment.setDeleted(0);
        appointmentMapper.insert(appointment);
        // 8. 返回预约 ID
        return appointment.getId();
    }

    /**
     * 取消预约
     */
    @Transactional
    @Override
    public void cancelAppointment(Long userId, Long appointmentId) {
        // 1. 校验预约是否存在
        Appointment appointment = appointmentMapper.findById(appointmentId);
        if (appointment == null) {
            throw new BusinessException(404, "预约不存在");
        }
        // 2. 校验预约是否属于当前用户
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权取消该预约");
        }
        // 3. 只有 PENDING/CONFIRMED 状态的预约才能取消
        if (!"PENDING".equals(appointment.getStatus()) && !"CONFIRMED".equals(appointment.getStatus())) {
            throw new BusinessException(400, "该预约无法取消");
        }
        // 4. 更新预约状态为 CANCELED
        int rows = appointmentMapper.updateStatus(appointment.getId(), userId, "CANCELED");
        if (rows == 0) {
            throw new BusinessException(500, "取消预约失败");
        }
        // 5. 退还积分
        AppointmentPackage packageEntity = packageMapper.findById(appointment.getPackageId());
        if (packageEntity != null) {
            userService.addPoints(userId, packageEntity.getPrice(), "APPOINTMENT_CANCEL",
                    "取消体检预约：" + packageEntity.getName());
        }
        // 6. 减少时段预约人数
        slotMapper.decrementCount(appointment.getSlotId());
    }

    /**
     * 查询用户的预约记录
     */
    @Override
    public List<Appointment> listMyAppointments(Long userId) {
        return appointmentMapper.findByUserId(userId);
    }
}
