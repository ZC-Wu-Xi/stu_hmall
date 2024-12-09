package com.hmall.trade.listener;

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
        System.err.println("收到了支付成功的消息:" + orderId + '\n' + "修改订单标记已支付");
        orderService.markOrderPaySuccess(orderId);
    }
}
