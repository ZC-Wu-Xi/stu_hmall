package com.hmall.common.config;

import com.hmall.common.interceptors.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ZC_Wu 汐
 * @date 2024/11/30 14:50
 * @description 配置类 要想生效必须被springmvc包扫描到
 * 在`resources`目录下的`META-INF/spring.factories`文件中配置了扫描
 */
@Configuration
@ConditionalOnClass(DispatcherServlet.class) // springMVC自动装配的条件注解，如果DispatcherServlet.class类存在该配置类就生效
// DispatcherServlet.class类是springMVC的核心API
// SpringCloudGateway：基于Spring的WebFlux技术，完全支持响应式编程，吞吐能力更强，但不支持SpringMVC
// 因此使用@ConditionalOnClass可以使网关微服务排除这个配置类，其他使用MVC的微服务都拥有这个配置类(该配置类在公共模块内)
public class MvcConfig implements WebMvcConfigurer {
    /**
     * 添加一个拦截器
     * 将我们写的拦截器注册进Mvc
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor()); // 不设置路径默认拦截所有路径
    }
}
