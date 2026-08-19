package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;
import com.wmm.eldercare.core.vo.AppointmentAdminListVO;

import java.util.List;

/**
 * 管理端预约管理 Service
 */
public interface AppointmentAdminService {

    /**
     * 分页查询预约列表（status 状态筛选，可空）
     */
    PageResult<AppointmentAdminListVO> listAppointments(String status, Integer pageNum, Integer pageSize);

    /**
     * 更新预约状态（确认/完成/取消）
     */
    void updateAppointmentStatus(Long id, String status);

    /**
     * 分页查询套餐列表
     */
    PageResult<AppointmentPackage> listPackages(Integer pageNum, Integer pageSize);

    /**
     * 新增套餐
     */
    void addPackage(AppointmentPackage appointmentPackage);

    /**
     * 修改套餐
     */
    void updatePackage(Long id, AppointmentPackage appointmentPackage);

    /**
     * 删除套餐
     */
    void deletePackage(Long id);

    /**
     * 查询某套餐的所有时段
     */
    List<AppointmentSlot> listSlots(Long packageId);

    /**
     * 新增时段
     */
    void addSlot(AppointmentSlot slot);

    /**
     * 删除时段
     */
    void deleteSlot(Long id);
}