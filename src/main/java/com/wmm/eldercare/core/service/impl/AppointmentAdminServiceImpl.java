package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.AppointmentMapper;
import com.wmm.eldercare.core.mapper.AppointmentPackageMapper;
import com.wmm.eldercare.core.mapper.AppointmentSlotMapper;
import com.wmm.eldercare.core.pojo.AppointmentPackage;
import com.wmm.eldercare.core.pojo.AppointmentSlot;
import com.wmm.eldercare.core.service.AppointmentAdminService;
import com.wmm.eldercare.core.vo.AppointmentAdminListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentAdminServiceImpl implements AppointmentAdminService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentPackageMapper packageMapper;
    private final AppointmentSlotMapper slotMapper;

    @Override
    public PageResult<AppointmentAdminListVO> listAppointments(String status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AppointmentAdminListVO> list = appointmentMapper.findAll(status);
        PageInfo<AppointmentAdminListVO> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    @Override
    public void updateAppointmentStatus(Long id, String status) {
        if (appointmentMapper.findById(id) == null) {
            throw new BusinessException(404, "预约不存在");
        }
        appointmentMapper.updateStatusAdmin(id, status);
    }

    @Override
    public PageResult<AppointmentPackage> listPackages(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AppointmentPackage> list = packageMapper.findAll();
        PageInfo<AppointmentPackage> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    @Override
    public void addPackage(AppointmentPackage appointmentPackage) {
        if (appointmentPackage.getName() == null || appointmentPackage.getName().isBlank()) {
            throw new BusinessException(400, "套餐名称不能为空");
        }
        appointmentPackage.setStatus("ENABLED");
        appointmentPackage.setCreateTime(LocalDateTime.now());
        appointmentPackage.setUpdateTime(LocalDateTime.now());
        appointmentPackage.setDeleted(0);
        packageMapper.insert(appointmentPackage);
    }

    @Override
    public void updatePackage(Long id, AppointmentPackage appointmentPackage) {
        if (packageMapper.findById(id) == null) {
            throw new BusinessException(404, "套餐不存在");
        }
        packageMapper.update(id, appointmentPackage);
    }

    @Override
    public void deletePackage(Long id) {
        if (packageMapper.findById(id) == null) {
            throw new BusinessException(404, "套餐不存在");
        }
        packageMapper.deleteById(id);
    }

    @Override
    public List<AppointmentSlot> listSlots(Long packageId) {
        if (packageMapper.findById(packageId) == null) {
            throw new BusinessException(404, "套餐不存在");
        }
        return slotMapper.findByPackageId(packageId);
    }

    @Override
    public void addSlot(AppointmentSlot slot) {
        slot.setStatus("AVAILABLE");
        slot.setCreateTime(LocalDateTime.now());
        slot.setUpdateTime(LocalDateTime.now());
        slot.setDeleted(0);
        slotMapper.insert(slot);
    }

    @Override
    public void deleteSlot(Long id) {
        if (slotMapper.findById(id) == null) {
            throw new BusinessException(404, "时段不存在");
        }
        slotMapper.deleteById(id);
    }
}