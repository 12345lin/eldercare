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
import com.wmm.eldercare.core.service.HealthRecordService;
import com.wmm.eldercare.core.vo.HealthTrendVO;
import com.wmm.eldercare.core.vo.IndicatorTrend;
import com.wmm.eldercare.core.vo.MonthlyHealthStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordMapper healthRecordMapper;

    private final UserMapper userMapper;

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
    }

    @Override
    public PageResult<HealthRecord> listHealthRecords(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<HealthRecord> list = healthRecordMapper.findByUserId(userId);
        PageInfo<HealthRecord> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    @Override
    public HealthRecord getHealthRecord(Long userId, Long id) {
        HealthRecord record = healthRecordMapper.findById(id, userId);
        if (record == null) {
            throw new BusinessException(404, "记录不存在");
        }
        return record;
    }

    @Override
    public void deleteHealthRecord(Long userId, Long id) {
        int rows = healthRecordMapper.deleteById(id, userId);
        if (rows == 0) {
            throw new BusinessException(404, "记录不存在或无权删除");
        }
    }

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