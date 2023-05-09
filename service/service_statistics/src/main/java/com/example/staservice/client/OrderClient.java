package com.example.staservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(name = "service-order", contextId="service-order-statistics")
public interface OrderClient {
    @GetMapping("/eduorder/order/orderCount")
    Integer orderCount();
}
