package com.hmall.trade.listener;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constants.MQConstants;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author ZC_Wu 汐
 * @date 2025/1/2 17:40
 * @description
 */
@Component
@RequiredArgsConstructor
public class OrderDelayListener {
    private final IOrderService orderService;
    private final PayClient payClient;
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(MQConstants.DELAY_ORDER_QUEUE_NAME),
        exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE_NAME, delayed = "true"),
        key = MQConstants.DELAY_ORDER_KEY
    ))
    public void listenOrderDelayMessage(Long orderId) {
        // 1. 检查支付状态
        Order order = orderService.getById(orderId);
        // 2. 检测订单状态，判断是否已支付
        if (order == null || order.getStatus() != 1) { // 1未支付
            // 订单不存在或已经支付
            return;
        }

        // 3. 未支付，查询支付流水状态
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);

        // 4. 判断是否已支付
        if (payOrder != null && payOrder.getStatus() == 3) { // 3支付成功
            // 4.1 已支付，标记订单状态已支付
            orderService.markOrderPaySuccess(orderId);
        } else {
            // 4.2 未支付，取消订单，恢复库存
            orderService.cancelOrder(orderId);
        }


    }

}
