package com.example.eduorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.commonutils.ordervo.CourseWebVoOrder;
import com.example.commonutils.ordervo.UcenterMemberOrder;
import com.example.eduorder.client.EduClient;
import com.example.eduorder.client.UcenterClient;
import com.example.eduorder.entity.Order;
import com.example.eduorder.entity.PayLog;
import com.example.eduorder.mapper.OrderMapper;
import com.example.eduorder.service.OrderService;
import com.example.eduorder.service.PayLogService;
import com.example.eduorder.utils.OrderNoUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>

 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;

    @Autowired
    PayLogService payLogService;

    @Override
    public String createOrder(String courseId, String memberId) {
        UcenterMemberOrder userInfoOrder = ucenterClient.getUserInfoOrder(memberId);
        CourseWebVoOrder courseInfoOrder = eduClient.getCourseInfoOrder(courseId);
        Order order = new Order();
//        order.setOrderNo(IdWorker.getIdStr());
        order.setOrderNo(OrderNoUtil.getOrderNo());
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfoOrder.getTitle());
        order.setCourseCover(courseInfoOrder.getCover());
        order.setTeacherId(courseInfoOrder.getTeacherId());
        order.setTotalFee(courseInfoOrder.getPrice());
        order.setMemberId(memberId);
        order.setMobile(userInfoOrder.getMobile());
        order.setNickname(userInfoOrder.getNickname());
        order.setStatus(0);
        order.setPayType(1);
        baseMapper.insert(order);


        return order.getOrderNo();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(Map<String, String> notifyMap) {
        // 更新订单状态
        String outTradeNo = notifyMap.get("out_trade_no");
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getOrderNo, outTradeNo);
        Order order = this.getOne(lqw);
        order.setStatus(1);//支付成功
        baseMapper.updateById(order);
        // 记录支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(outTradeNo);
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型：微信支付
        payLog.setTotalFee(BigDecimal.valueOf(Long.parseLong(notifyMap.get("total_fee"))));
        payLog.setTradeState(notifyMap.get("result_code"));
        payLog.setTransactionId(notifyMap.get("transaction_id"));
        payLog.setAttr(new Gson().toJson(notifyMap));
        payLogService.save(payLog);
    }
}
