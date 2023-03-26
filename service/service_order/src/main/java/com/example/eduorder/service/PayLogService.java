package com.example.eduorder.service;

import com.example.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 */
public interface PayLogService extends IService<PayLog> {

    Map createNative(String orderNo);

    Map<String, String> getPayStatus(String orderNo);

    void updateOrderStatus(Map<String, String> map);
}
