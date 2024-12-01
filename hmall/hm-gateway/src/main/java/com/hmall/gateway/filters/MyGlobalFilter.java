package com.hmall.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ZC_Wu 汐
 * @date 2024/11/27 11:28
 * @description 演示全局过滤器
 */
//@Component // 现在我们不需要该过滤器
public class MyGlobalFilter implements GlobalFilter, Ordered {
    /**
     * 处理请求并将其传递给下一个过滤器
     *
     * @param exchange 当前请求的上下文，包含整个过滤器链内的共享数据，如request、response等各种数据
     * @param chain    过滤器链，基于它向下传递请求。要调用过滤器链的下一个过滤器
     * @return 根据返回值标记当前请求是否被完成或拦截，chain.filter(exchange)就放行了。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // TODO 模拟登录验证逻辑
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        System.out.println("headers = " + headers);
        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 过滤器执行顺序，值越小，优先级越高
        return 1;
    }
}
