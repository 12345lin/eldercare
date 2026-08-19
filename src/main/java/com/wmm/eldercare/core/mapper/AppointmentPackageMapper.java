package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.AppointmentPackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppointmentPackageMapper {

    /**
     * 查询所有可用套餐
     */
    List<AppointmentPackage> listEnabled();

    /**
     * 根据 ID 查询套餐
     */
    AppointmentPackage findById(@Param("id") Long id);

    /**
     * 管理端分页查询所有套餐
     */
    List<AppointmentPackage> findAll();

    /**
     * 管理端新增套餐
     */
    int insert(AppointmentPackage appointmentPackage);

    /**
     * 管理端修改套餐（动态 SQL）
     */
    int update(@Param("id") Long id, @Param("appointmentPackage") AppointmentPackage appointmentPackage);

    /**
     * 管理端逻辑删除套餐
     */
    int deleteById(@Param("id") Long id);
}
