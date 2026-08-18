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
}
