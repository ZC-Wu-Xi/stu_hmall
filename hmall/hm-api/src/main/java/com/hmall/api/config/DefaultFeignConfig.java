package com.hmall.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @author ZC_Wu 汐
 * @date 2024/11/26 15:01
 * @description openFeign 的配置
 */
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // feign的日志级别
    }
}
