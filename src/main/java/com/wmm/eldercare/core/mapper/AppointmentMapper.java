package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppointmentMapper {

    /**
     * 插入预约记录
     */
    int insert(Appointment appointment);

    /**
     * 根据 ID 查询预约
     */
    Appointment findById(@Param("id") Long id);

    /**
     * 查询用户的预约记录
     */
    List<Appointment> findByUserId(@Param("userId") Long userId);

    /**
     * 更新预约状态
     */
    int updateStatus(@Param("id") Long id, 
                     @Param("userId") Long userId, 
                     @Param("status") String status);
}
