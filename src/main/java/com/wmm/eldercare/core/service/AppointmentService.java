package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.pojo.Appointment;
import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    /**
     * 查询所有可用体检套餐
     */
    List<AppointmentPackage> listPackages();

    /**
     * 查询某套餐在某日期的可预约时段
     */
    List<AppointmentSlot> listSlots(Long packageId, LocalDate date);

    /**
     * 预约体检
     */
    Long bookAppointment(Long userId, Long slotId);

    /**
     * 取消预约
     */
    void cancelAppointment(Long userId, Long appointmentId);

    /**
     * 查询我的预约记录
     */
    List<Appointment> listMyAppointments(Long userId);
}
