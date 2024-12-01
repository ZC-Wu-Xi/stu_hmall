package com.hmall.gateway.filters;


import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.util.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author ZC_Wu 汐
 * @date 2024/11/29 17:57
 * @description 与权限有关的全局过滤器 登录校验
 * 这里我们不做权限校验，只做登录校验
 */
@Component
@RequiredArgsConstructor // 必需的构造函数
//@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;
    // Spring 给我们提供的antPath的匹配器
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 过滤逻辑
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取require对象
        ServerHttpRequest request = exchange.getRequest();

        // 2. 判断是否需要做登录拦截
        if (isExclude(request.getPath().toString())) {
            // 放行
            return chain.filter(exchange);
        }

        // 3. 从请求头获取token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (headers != null && !headers.isEmpty()) {
            token = headers.get(0);
        }

        // 4. 校验并解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            // 如果无效拦截，设置响应状态码为401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 设置响应状态码401
            return response.setComplete(); // 终止
        }

        // 5. 传递用户信息
        String userInfo = userId.toString();
        ServerWebExchange swe = exchange.mutate() // mutate就是对下游请求做更改
                .request(builder -> builder.header("user-info", userInfo)) // 对下游请求添加请求头
                .build();

        // 6. 放行
        return chain.filter(swe);
    }

    /**
     * 判断该路径是否应该放行
     * 是否需要登录拦截
     * @param path
     * @return
     */
    private boolean isExclude(String path) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, path)) { // 是否匹配
                return true;
            }
        }
        return false;
    }

    // 过滤器的优先级，order越小优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
