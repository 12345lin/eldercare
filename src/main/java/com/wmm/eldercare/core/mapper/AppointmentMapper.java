package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.Appointment;
import com.wmm.eldercare.core.vo.AppointmentAdminListVO;
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
     * 统计某用户预约总次数（个人中心统计面板）
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 更新预约状态
     */
    int updateStatus(@Param("id") Long id,
                     @Param("userId") Long userId,
                     @Param("status") String status);

    /**
     * 管理端分页查询预约列表（status 状态筛选，可空；JOIN user/package/slot 显示详情）
     */
    List<AppointmentAdminListVO> findAll(@Param("status") String status);

    /**
     * 管理端更新预约状态（无 userId 校验）
     */
    int updateStatusAdmin(@Param("id") Long id, @Param("status") String status);

    /**
     * 关联体检报告（更新 report_url、原始文件名、上传时间、上传管理员）
     */
    int updateReport(@Param("id") Long id,
                     @Param("reportUrl") String reportUrl,
                     @Param("originalFilename") String originalFilename,
                     @Param("uploadAdminId") Long uploadAdminId);
}