package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysConfigMapper {

    /**
     * 根据配置键查询配置值
     */
    SysConfig findByKey(@Param("configKey") String configKey);
}
