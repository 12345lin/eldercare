package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.SysConfigMapper;
import com.wmm.eldercare.core.pojo.SysConfig;
import com.wmm.eldercare.core.service.ConfigAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ConfigAdminServiceImpl implements ConfigAdminService {

    private final SysConfigMapper sysConfigMapper;

    /**
     * 分页查询配置列表（keyword 空查全部，非空按 key/描述模糊搜索）
     *
     * @param keyword  搜索关键字（可空）
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public PageResult<SysConfig> listConfigs(String keyword, Integer pageNum, Integer pageSize) {
        //1.开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        //2.封装分页结果
        List<SysConfig> list = sysConfigMapper.findAll(keyword);
        PageInfo<SysConfig> pageInfo = new PageInfo<>(list);

        //3.返回分页结果
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageNum,
                pageSize,
                pageInfo.getPages()
        );
    }

    /**
     * 新增配置（校验 configKey 非空且不重复）
     * @param config 配置内容（configKey/configValue/description）
     */
    @Override
    public void addConfig(SysConfig config) {
        //1.校验 configKey 非空且不重复
        if (config.getConfigKey() == null || config.getConfigKey().isEmpty()) {
            throw new BusinessException(400, "配置键不能为空");
        }
        if (sysConfigMapper.findByKey(config.getConfigKey()) != null) {
            throw new BusinessException(400, "配置键已存在");
        }
        //2.补全公共字段默认值
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        config.setDeleted(0);
        //3.新增配置
        sysConfigMapper.insert(config);
    }

    /**
     * 修改配置（configKey 不可修改，只更新 configValue/description）
     * @param id     配置 ID
     * @param config 修改内容（configValue/description）
     */
    @Override
    public void updateConfig(Long id, SysConfig config) {
        //1.校验配置是否存在
        SysConfig existingConfig = sysConfigMapper.findById(id);
        if (existingConfig == null) {
            throw new BusinessException(400, "配置不存在");
        }
        //2.更新配置
        sysConfigMapper.update(id, config);
    }

    /**
     * 逻辑删除配置
     * @param id 配置 ID
     */
    @Override
    public void deleteConfig(Long id) {
        if (sysConfigMapper.findById(id) == null) {
            throw new BusinessException(400, "配置不存在");
        }
        //2.逻辑删除配置
        sysConfigMapper.deleteById(id);
    }
}