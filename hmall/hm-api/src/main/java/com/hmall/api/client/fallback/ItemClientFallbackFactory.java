package com.hmall.api.client.fallback;

import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/2 17:13
 * @description openFeign远程调用item-service被sentinel熔断降级的fallback处理逻辑
 * 还需要在配置类里把ItemClientFallbackFactory注册为bean
 * 我们在DefaultFeignConfig里将该类注册为了bean
 */
@Slf4j
public class ItemClientFallbackFactory implements FallbackFactory<ItemClient> {
    /**
     * @param cause 异常 远程调用出问题了 或者 限流了 等等
     * @return 自定义的OpenFeignApi
     */
    @Override
    public ItemClient create(Throwable cause) {
        log.error("商品微服务出现异常，准备fallback");
        return new ItemClient() {
            // 失败的兜底逻辑
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("查询商品失败！fallback", cause);
                return CollUtils.emptyList();
            }

            // 失败的兜底逻辑
            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("扣减商品库存失败！fallback", cause);
                throw new RuntimeException(cause);
            }
        };
    }
}
