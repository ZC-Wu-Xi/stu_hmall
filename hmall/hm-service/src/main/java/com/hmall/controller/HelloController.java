package com.hmall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("hi")
public class HelloController {

    private final Map<String, AtomicInteger> countMap = new HashMap<>();

    @GetMapping
    public String hello(HttpServletRequest request) throws InterruptedException {
        Thread.sleep(300);
        String ip = request.getRemoteAddr(); // 获取客户端 IP 地址
        AtomicInteger ai = countMap.get(ip);
        if (ai == null) {
            ai = new AtomicInteger(0);
            countMap.put(ip, ai);
        }
        // 自增并返回用户访问次数
        return String.format("<h5>欢迎访问黑马商城, 这是您第%d次访问<h5>", ai.incrementAndGet());
    }
}
