package com.wmm.eldercare.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置属性
 * <p>配置项在 application.yaml 的 aliyun.oss 下；AccessKey 通过环境变量读取，不写进代码。</p>
 *
 * @author wmm
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {
    /** 访问域名，如 https://oss-cn-beijing.aliyuncs.com */
    private String endpoint;
    /** 存储桶名称，如 wmmya */
    private String bucketName;
    /** 区域，如 cn-beijing */
    private String region;
}
