package com.example.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.eduorder.client.EduClient;
import com.example.eduorder.entity.Order;
import com.example.eduorder.entity.PayLog;
import com.example.eduorder.mapper.PayLogMapper;
import com.example.eduorder.service.OrderService;
import com.example.eduorder.service.PayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eduorder.utils.HttpClient;
import com.example.servicebase.exceptionhandler.EduException;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>

 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {
    @Autowired
    private OrderService orderService;

    @Autowired
    private EduClient eduClient;
    @Override
    public Map createNative(String orderNo) {
        try {
            LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Order::getOrderNo, orderNo);
            Order order = orderService.getOne(lqw);
            Map m = new HashMap<>();
//            m.put("appid", "wx74862e0dfcf69954");
//            m.put("mch_id", "1558950191");
            m.put("appid", "wx011c535fecded0af");
            m.put("mch_id", "1642365916");
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            m.put("body", order.getCourseTitle());
            m.put("out_trade_no", orderNo);
            m.put("total_fee", order.getTotalFee().multiply(new
                    BigDecimal("100")).longValue() + "");
            m.put("spbill_create_ip", "127.0.0.1");
//            m.put("notify_url",
//                    "http://guli.shop/api/order/weixinPay/weixinNotify\n");
                        m.put("notify_url",
                    "http://localhost:8007/eduorder/paylog/weixinNotify\n");
            m.put("trade_type", "NATIVE");
          System.err.println(m);

            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
//            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "MkFanHCM26N6ZI4aMBVZoQwCaD6Ms7Ew"));
            client.setHttps(true);
            client.post();

            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
          System.err.println(resultMap);

            Map map = new HashMap<>();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));
            map.put("code_url", resultMap.get("code_url"));

            return map;
        } catch (Exception e) {
            throw new EduException(201, "生成二维码失败");
        }
    }

    @Override
    public Map<String, String> getPayStatus(String orderNo) {
        try {
            //1、封装参数
            Map m = new HashMap<>();
//            m.put("appid", "wx74862e0dfcf69954");
//            m.put("mch_id", "1558950191");
            m.put("appid", "wx011c535fecded0af");
            m.put("mch_id", "1642365916");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            //2、设置请求
            HttpClient client = new
                    HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m,
                    "MkFanHCM26N6ZI4aMBVZoQwCaD6Ms7Ew"));
//            client.setXmlParam(WXPayUtil.generateSignedXml(m,
//                    "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateOrderStatus(Map<String, String> map) {
        String orderNo = map.get("out_trade_no");
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getOrderNo, orderNo);
        Order order = orderService.getOne(lqw);

        if(order.getStatus().intValue()==1) {
            return;
        }

        order.setStatus(1);
        orderService.updateById(order);

        // 课程购买数量加一
        eduClient.updateCourseBuyCount(order.getCourseId());

        PayLog payLog = new PayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(JSONObject.toJSONString(map));

        baseMapper.insert(payLog);
    }
}
