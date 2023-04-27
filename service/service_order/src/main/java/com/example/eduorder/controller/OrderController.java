package com.example.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commonutils.JwtUtils;
import com.example.commonutils.R;
import com.example.eduorder.entity.Order;
import com.example.eduorder.entity.vo.OrderQuery;
import com.example.eduorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("getOrderList/{userId}")
    public R getOrderList(@PathVariable String userId){
        LambdaQueryWrapper<Order> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMemberId,userId);
        return R.ok().data("orderList",orderService.list(wrapper));
    }

    @GetMapping("getBoughtCourse/{memberId}")
    public List<String> getBoughtCourse(@PathVariable String memberId){
        System.err.println(memberId);
        LambdaQueryWrapper<Order> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMemberId,memberId);
        wrapper.eq(Order::getStatus,1);
        wrapper.select(Order::getCourseId);
        List<String> collect = orderService.list(wrapper).stream().map(Order::getCourseId).collect(Collectors.toList());
        return collect;
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


    //条件查询带分页的方法
    @PostMapping("pageOrderCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) OrderQuery orderQuery) {
        Page<Order> pageOrder = new Page<>(current, limit);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        //多条件组合查询
        String orderNo = orderQuery.getOrderNo();
        Integer status = orderQuery.getStatus();
        Integer payType = orderQuery.getPayType();
        String courseId = orderQuery.getCourseId();
        String memberId = orderQuery.getMemberId();
        String begin = orderQuery.getBegin();
        String end = orderQuery.getEnd();
        //判断条件值是否为空，如果不为空拼接条件
        if (!StringUtils.isEmpty(orderNo)) {
            wrapper.eq("order_no", orderNo);
        }

        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }

        if (!StringUtils.isEmpty(payType)) {
            wrapper.eq("pay_type", payType);
        }

        if (!StringUtils.isEmpty(courseId)) {
            wrapper.eq("course_id", courseId);
        }

        if (!StringUtils.isEmpty(memberId)) {
            wrapper.eq("member_id", memberId);
        }

        if (!StringUtils.isEmpty(begin)) {
            wrapper.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            wrapper.le("gmt_create", end);
        }

        wrapper.orderByDesc("gmt_create");
        orderService.page(pageOrder, wrapper);
        long total = pageOrder.getTotal();
        List<Order> records = pageOrder.getRecords();
        return R.ok().data("total", total).data("rows", records);
    }
}

