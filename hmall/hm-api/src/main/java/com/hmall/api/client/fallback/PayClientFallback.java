package com.hmall.api.client.fallback;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author ZC_Wu 汐
 * @date 2025/1/2 17:52
 * @description
 */
@Slf4j
public class PayClientFallback implements FallbackFactory<PayClient> {
    /**
     * 如果查询失败返回null
     * @param cause
     * @return
     */
    @Override
    public PayClient create(Throwable cause) {
        return new PayClient() {
            @Override
            public PayOrderDTO queryPayOrderByBizOrderNo(Long id) {
                return null;
            }
        };
    }
}