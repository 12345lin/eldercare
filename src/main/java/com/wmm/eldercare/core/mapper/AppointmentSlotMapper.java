package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.AppointmentSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AppointmentSlotMapper {

    /**
     * 根据套餐 ID 和日期查询时段列表
     */
    List<AppointmentSlot> findByPackageIdAndDate(@Param("packageId") Long packageId, 
                                                  @Param("date") LocalDate date);

    /**
     * 管理端按套餐 ID 查询所有时段
     */
    List<AppointmentSlot> findByPackageId(@Param("packageId") Long packageId);

    /**
     * 根据 ID 查询时段
     */
    AppointmentSlot findById(@Param("id") Long id);

    /**
     * 管理端新增时段
     */
    int insert(AppointmentSlot slot);

    /**
     * 管理端修改时段（动态 SQL）
     */
    int update(@Param("id") Long id, @Param("slot") AppointmentSlot slot);

    /**
     * 管理端逻辑删除时段
     */
    int deleteById(@Param("id") Long id);

    /**
     * 增加已预约人数（防并发）
     * 返回影响行数，0 表示名额已满
     */
    int incrementCount(@Param("id") Long id);

    /**
     * 减少已预约人数（取消预约时）
     */
    int decrementCount(@Param("id") Long id);
}
