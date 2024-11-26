package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author ZC_Wu 汐
 * @date 2024/11/26 16:18
 * @description
 */
@FeignClient("trade-service")
public interface TradeClient {
    // 标记订单已支付
    @PutMapping("/orders/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);

}
