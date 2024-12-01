package com.hmall.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/1 16:14
 * @description 配置动态路由
 */
@Slf4j
@Component
@RequiredArgsConstructor // 必需的构造函数
public class DynamicRouteLoader {
    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter writer;

    // 路由配置文件的id和分组
    private final String dataId = "gateway-routes.json"; // nacos的配置文件名
    private final String group = "DEFAULT_GROUP";
    private final Set<String> routeIds = new HashSet<>();

    @PostConstruct // 在bean初始化后执行
    public void initRouteConfigListener() throws NacosException {
        // 1. 项目启动时，先拉取一次配置，并且添加配置监听器
        // configInfo 实际上就是json串，里面存的就是路由信息
        String configInfo = nacosConfigManager.getConfigService()
                // 监听路由配置变化
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        // 2. 监听到配置变更，需要去更新gateway路由表
                        updateConfigInfo(configInfo);
                    }
                });
        // 3. 第一次读取配置，也需要更新路由表
        updateConfigInfo(configInfo);

    }

    public void updateConfigInfo(String configInfo) {
        log.debug("监听的路由配置信息：{}", configInfo);
        // 1. 解析配置信息，转为RouteDefinition
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        // 2. 删除旧的路由表
        for (String routeId : routeIds) {
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        // 3. 更新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            // 3.1 更新路由表
            writer.save(Mono.just(routeDefinition)).subscribe();
            // 3.2 记录路由id便于下次更新时删除
            routeIds.add(routeDefinition.getId());
        }
    }
}
