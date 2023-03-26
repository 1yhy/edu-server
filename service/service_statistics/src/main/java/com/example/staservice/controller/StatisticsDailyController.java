package com.example.staservice.controller;


import com.example.commonutils.R;
import com.example.staservice.service.StatisticsDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>

 */
@RestController
@RequestMapping("/staservice/sta")
//@CrossOrigin
@EnableScheduling
public class StatisticsDailyController {

    @Autowired
    private StatisticsDailyService staService;

    @PostMapping("registerCount/{day}")
    public R registerCount(@PathVariable String day){
        staService.registerCount(day);
        return R.ok();
    }

    @GetMapping("showData/{type}/{begin}/{end}")
    public R showData(@PathVariable String type,@PathVariable String begin,@PathVariable String end){
       Map<String,Object> map = staService.getShowData(type,begin,end);
       return R.ok().data(map);
    }
}

