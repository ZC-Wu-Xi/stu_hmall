package com.hmall.trade.listener;

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
 * @date 2024/12/9 17:45
 * @description 使用rabbitMQ异步调用 消息接收者
 * 监听支付成功消息 标记订单已支付
 */
@Component
@RequiredArgsConstructor
public class PayStatusListener {
    private final IOrderService orderService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("trade.pay.success.queue"),
            exchange = @Exchange(name = "pay.direct"), // 省略的交换机的类型，默认就是direct
            key = "pay.success"))
    public void listenPaySuccess(Long orderId) {
        System.out.println("收到了支付成功的消息:" + orderId + '\n' + "修改订单标记已支付");
        // 1. 查询订单
        Order order = orderService.getById(orderId);

        // 2. 判断订单状态是否为未支付(是否重复发送该消息)
        if (order == null || order.getStatus() != 1) { // 不是未支付
            // 不做处理
            return;
        }

        // 3. 标记订单为已支付
        orderService.markOrderPaySuccess(orderId);
    }
}
