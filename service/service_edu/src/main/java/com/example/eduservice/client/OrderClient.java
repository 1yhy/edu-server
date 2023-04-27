package com.example.eduservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient("service-order")
public interface OrderClient {
    @GetMapping("/eduorder/order/isBuy/{courseId}/{memberId}")
    public boolean isBuy(@PathVariable("courseId") String courseId, @PathVariable("memberId") String memberId);

    @GetMapping("/eduorder/order/getBoughtCourse/{memberId}")
    public List<String> getBoughtCourse(@PathVariable String memberId);
}
