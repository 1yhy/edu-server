package com.example.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.commonutils.JwtUtils;
import com.example.commonutils.R;
import com.example.eduorder.entity.Order;
import com.example.eduorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/eduorder/order")
//@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("createOrder/{courseId}")
    public R createOrder(@PathVariable String courseId, HttpServletRequest request){
        String orderNo = orderService.createOrder(courseId, JwtUtils.getMemberIdByJwtToken(request));
        return R.ok().data("orderId",orderNo);
    }

    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId){
        LambdaQueryWrapper<Order> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo,orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item",order);
    }

    @GetMapping("isBuy/{courseId}/{memberId}")
    public boolean isBuy(@PathVariable String courseId,@PathVariable String memberId){
        LambdaQueryWrapper<Order> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCourseId,courseId);
        wrapper.eq(Order::getMemberId,memberId);
        wrapper.eq(Order::getStatus,1);
        return orderService.count(wrapper)>0;
    }

    @GetMapping("orderCount")
    public Integer orderCount(){
        LambdaQueryWrapper<Order> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus,1);
        return orderService.count(wrapper);
    }


}

