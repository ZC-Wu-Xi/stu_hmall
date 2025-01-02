package com.hmall.common.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/9 17:42
 * @description rabbitMQ的配置
 * 使用json消息转换器
 */
@Configuration
//@ConditionalOnClass(MessageConverter.class) // springMVC自动装配的条件注解，如果MessageConverter.class类存在该配置类就生效
// MessageConverter是amqp即rabbitMQ遵循的协议
@ConditionalOnClass(RabbitTemplate.class)
public class MqConfig {
    @Bean
    public MessageConverter messageConverter() {
        // 使用json消息转换器
        // 默认情况下Spring采用的序列化方式是JDK序列化。众所周知，JDK序列化存在下列问题：
        //  - 数据体积过大
        //  - 有安全漏洞
        //  - 可读性差
        return new Jackson2JsonMessageConverter();
    }
}
