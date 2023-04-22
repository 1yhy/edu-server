package com.example.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.commonutils.R;
import com.example.eduorder.entity.Order;
import com.example.eduorder.service.OrderService;
import com.example.eduorder.service.PayLogService;
import com.example.eduorder.utils.StreamUtils;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/eduorder/paylog")
@Slf4j
//@CrossOrigin
public class PayLogController {
    @Autowired
    private PayLogService payLogService;

    @Autowired
    private OrderService orderService;

    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo) {
        Map map = payLogService.createNative(orderNo);
        return R.ok().data(map);
    }

    /**
     * 功能描述：微信回调通知
     *
     * @author cakin
     * @date 2021/1/13
     * @param request 请求
     * @return response 响应
     */
    @PostMapping("weixinNotify")
    public String wxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("\n callback/notify 被调用");
        ServletInputStream inputStream = request.getInputStream();
        String notifyXml = StreamUtils.inputStream2String(inputStream, "utf-8");
        log.info("\n notifyXml = \n " + notifyXml);
        // 验签：验证签名是否正确
        if (WXPayUtil.isSignatureValid(notifyXml, "MkFanHCM26N6ZI4aMBVZoQwCaD6Ms7Ew")) {
            // 解析返回结果
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyXml);
            // 判断支付是否成功
            if ("SUCCESS".equals(notifyMap.get("result_code"))) {
                // 金额校验
                String totalFee = notifyMap.get("total_fee"); //支付结果返回的订单金额
                String outTradeNo = notifyMap.get("out_trade_no");//订单号
                LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
                lqw.eq(Order::getOrderNo, outTradeNo);
                Order order = orderService.getOne(lqw); //查询本地订单
                // 校验返回的订单金额是否与商户侧的订单金额一致
                if (order != null && order.getTotalFee().intValue() == Integer.parseInt(totalFee)) {
                    // 接口调用的幂等性：无论接口被调用多少次，最后所影响的结果都是一致的
                    if (order.getStatus() == 0) {
                        // 更新订单状态
                        orderService.updateOrderStatus(notifyMap);
                    }
                    // 支付成功：给微信发送我已接收通知的响应
                    // 创建响应对象
                    Map<String, String> returnMap = new HashMap<>();
                    returnMap.put("return_code", "SUCCESS");
                    returnMap.put("return_msg", "OK");
                    log.info("支付成功，通知已处理");
//                    return R.ok().message("支付成功").data("returnMap", returnMap);
                    return "redirect:http://localhost:8990/course/" + order.getCourseId();
                }

            }
        }

        // 创建响应对象：微信接收到校验失败的结果后，会反复的调用当前回调函数
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("return_code", "FAIL");
        returnMap.put("return_msg", "");
        String returnXml = WXPayUtil.mapToXml(returnMap);
        log.info("支付失败，通知已处理");
        return returnXml;
    }


    @GetMapping("payStatus/{orderNo}")
    public R getPayStatus(@PathVariable String orderNo){
        Map<String,String> map =payLogService.getPayStatus(orderNo);
        if(map==null){
            return R.error().message("支付出错");
        }

        if(map.get("trade_state").equals("SUCCESS")){
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }

        return R.ok().code(25000).message("支付中");
    }
}

