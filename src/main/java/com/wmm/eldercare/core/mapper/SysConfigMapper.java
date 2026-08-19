package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysConfigMapper {

    /**
     * 根据配置键查询配置值
     */
    SysConfig findByKey(@Param("configKey") String configKey);

    /**
     * 新增配置
     */
    int insert(SysConfig config);

    /**
     * 根据 ID 查询配置
     */
    SysConfig findById(@Param("id") Long id);

    /**
     * 分页查询配置列表（keyword 为空查全部，非空按 config_key/description 模糊搜索）
     */
    List<SysConfig> findAll(@Param("keyword") String keyword);

    /**
     * 修改配置（动态 SQL，只更新有值的字段）
     */
    int update(@Param("id") Long id, @Param("config") SysConfig config);

    /**
     * 逻辑删除配置
     */
    int deleteById(@Param("id") Long id);
}
