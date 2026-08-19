package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.pojo.SysConfig;

/**
 * 管理端系统配置 Service
 */
public interface ConfigAdminService {

    /**
     * 分页查询配置列表（keyword 空查全部，非空按 key/描述模糊搜索）
     *
     * @param keyword  搜索关键字（可空）
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<SysConfig> listConfigs(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 新增配置（校验 configKey 非空且不重复）
     *
     * @param config 配置内容（configKey/configValue/description）
     */
    void addConfig(SysConfig config);

    /**
     * 修改配置
     *
     * @param id     配置 ID
     * @param config 修改内容（configValue/description）
     */
    void updateConfig(Long id, SysConfig config);

    /**
     * 逻辑删除配置
     *
     * @param id 配置 ID
     */
    void deleteConfig(Long id);
}