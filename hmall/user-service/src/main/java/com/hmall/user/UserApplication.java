package com.hmall.user;

import com.hmall.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableFeignClients // 开启 OpenFeign 远程调用
//@EnableFeignClients(basePackages = "com.hmall.api.client") // 扫描openfeign的暴露接口包 开启 OpenFeign 远程调用
@EnableFeignClients(
        basePackages = "com.hmall.api.client",
defaultConfiguration = DefaultFeignConfig.class) // 使openFeign自定义配置类cart全局生效(配置了openfeign日志级别为full)
@MapperScan("com.hmall.user.mapper")
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}