package com.example.eduorder.service;

import com.example.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 */
public interface OrderService extends IService<Order> {

    String createOrder(String courseId, String memberId);
}
