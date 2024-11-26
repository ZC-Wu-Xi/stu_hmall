package com.hmall.cart;

import com.hmall.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@EnableFeignClients // 开启 OpenFeign 远程调用
//@EnableFeignClients(basePackages = "com.hmall.api.client") // 扫描openfeign的暴露接口包 开启 OpenFeign 远程调用
@EnableFeignClients(
        basePackages = "com.hmall.api.client",
defaultConfiguration = DefaultFeignConfig.class) // 使openFeign自定义配置类cart全局生效(配置了openfeign日志级别为full)
@MapperScan("com.hmall.cart.mapper")
@SpringBootApplication
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

    // 注入RestTemplate，实现Http请求的发送
    // 不需要这个了  我们用openFeign优化掉了
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
}