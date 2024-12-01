package com.hmall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/1 15:52
 * @description nacos配置热更新 从nacos配置文件cart-service.yaml中读取hm.cart.maxItems
 */
@Data
@Component
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {
    private Integer maxItems; // 购物车商品数量上限
}
