package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.dto.HealthRecordAddDTO;
import com.wmm.eldercare.core.mapper.HealthRecordMapper;
import com.wmm.eldercare.core.mapper.UserMapper;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.service.HealthGuardService;
import com.wmm.eldercare.core.service.HealthRecordService;
import com.wmm.eldercare.core.vo.HealthTrendVO;
import com.wmm.eldercare.core.vo.IndicatorTrend;
import com.wmm.eldercare.core.vo.MonthlyHealthStatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordMapper healthRecordMapper;

    private final UserMapper userMapper;

    private final HealthGuardService healthGuardService;

    /**
     * 添加健康记录
     * @param userId
     * @param dto
     */
    @Override
    public void addHealthRecord(Long userId, HealthRecordAddDTO dto) {
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUserId(userId);
        healthRecord.setSystolic(dto.getSystolic());
        healthRecord.setDiastolic(dto.getDiastolic());
        healthRecord.setBloodSugar(dto.getBloodSugar());
        healthRecord.setHeartRate(dto.getHeartRate());
        healthRecord.setWeight(dto.getWeight());
        healthRecord.setMemo(dto.getMemo());

        // BMI 计算：体重(kg) / 身高(m)²，身高单位是 cm，先换算成米再计算
        // 用户没填身高或体重时，BMI 保持 null，不抛异常（录入不强制身高）
        User user = userMapper.findUserById(userId);
        if (user != null && user.getHeight() != null
                && user.getHeight().compareTo(BigDecimal.ZERO) > 0
                && dto.getWeight() != null) {
            BigDecimal heightM = user.getHeight().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal bmi = dto.getWeight().divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
            healthRecord.setBmi(bmi);
        }

        healthRecord.setRecordedAt(LocalDateTime.now());
        healthRecord.setCreateTime(LocalDateTime.now());
        healthRecord.setUpdateTime(LocalDateTime.now());
        healthRecord.setDeleted(0);
        healthRecordMapper.insert(healthRecord);

        // 触发健康提醒（指标超标时生成个性化建议并推送站内消息）
        try {
            healthGuardService.checkAndNotify(userId, healthRecord);
        } catch (Exception e) {
            // 健康提醒失败不影响录入主流程
            log.warn("健康提醒生成失败 userId={}: {}", userId, e.getMessage());
        }
    }

    /**
     * 分页查询某用户所有健康记录
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<HealthRecord> listHealthRecords(Long userId, Integer pageNum, Integer pageSize) {
        //1. 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        //2. 执行查询
        List<HealthRecord> list = healthRecordMapper.findByUserId(userId);
        //3. 组装分页结果
        PageInfo<HealthRecord> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    /**
     * 查询某用户某条健康记录详情
     * @param userId
     * @param id
     * @return
     */
    @Override
    public HealthRecord getHealthRecord(Long userId, Long id) {
        HealthRecord record = healthRecordMapper.findById(id, userId);
        if (record == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return record;
    }

    /**
     * 逻辑删除某用户某条健康记录
     * @param userId
     * @param id
     */
    @Override
    public void deleteHealthRecord(Long userId, Long id) {
        int rows = healthRecordMapper.deleteById(id, userId);
        if (rows == 0) {
            throw new BusinessException(404, "记录不存在或无权删除");
        }
    }

    /**
     * 健康趋势分析：按月份聚合最近 6 个月的平均值/最大值/最小值
     * @param userId
     * @return
     */
    @Override
    public HealthTrendVO getTrend(Long userId) {
        List<MonthlyHealthStatVO> stats = healthRecordMapper.selectMonthlyStats(userId, 6);

        // 按月份顺序组装趋势数据，指标按月对齐
        List<String> months = new ArrayList<>();
        IndicatorTrend systolic   = new IndicatorTrend(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IndicatorTrend diastolic  = new IndicatorTrend(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IndicatorTrend bloodSugar = new IndicatorTrend(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IndicatorTrend heartRate  = new IndicatorTrend(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IndicatorTrend bmi        = new IndicatorTrend(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        for (MonthlyHealthStatVO s : stats) {
            months.add(s.getMonth());
            systolic.getAvg().add(s.getAvgSystolic());
            systolic.getMax().add(s.getMaxSystolic());
            systolic.getMin().add(s.getMinSystolic());

            diastolic.getAvg().add(s.getAvgDiastolic());
            diastolic.getMax().add(s.getMaxDiastolic());
            diastolic.getMin().add(s.getMinDiastolic());

            bloodSugar.getAvg().add(s.getAvgBloodSugar());
            bloodSugar.getMax().add(s.getMaxBloodSugar());
            bloodSugar.getMin().add(s.getMinBloodSugar());

            heartRate.getAvg().add(s.getAvgHeartRate());
            heartRate.getMax().add(s.getMaxHeartRate());
            heartRate.getMin().add(s.getMinHeartRate());

            bmi.getAvg().add(s.getAvgBmi());
            bmi.getMax().add(s.getMaxBmi());
            bmi.getMin().add(s.getMinBmi());
        }

        HealthTrendVO vo = new HealthTrendVO();
        vo.setMonths(months);
        vo.setSystolic(systolic);
        vo.setDiastolic(diastolic);
        vo.setBloodSugar(bloodSugar);
        vo.setHeartRate(heartRate);
        vo.setBmi(bmi);
        return vo;
    }
}