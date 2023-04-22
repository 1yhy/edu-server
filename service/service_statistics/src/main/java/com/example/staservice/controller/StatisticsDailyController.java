package com.example.staservice.controller;


import com.example.commonutils.R;
import com.example.staservice.model.dto.AdminInfoDTO;
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

    @PostMapping("viewCountDaily/{day}")
    public R registerCount(@PathVariable String day){
        staService.viewCountDaily(day);
        return R.ok();
    }

    // 更新注册人数
    @PostMapping("addOrUpdateRegisterCount/{day}")
    public R addOrUpdateRegisterCount(@PathVariable String day){
        staService.addOrUpdateRegisterCount(day);
        return R.ok();
    }

    // 更新登录人数
    @PostMapping("addOrUpdateLoginCount/{day}")
    public R addOrUpdateLoginCount(@PathVariable String day){
        staService.addOrUpdateLoginCount(day);
        return R.ok();
    }


    // 更新视频观看人数
    @PostMapping("addOrUpdateVideoViewCount/{day}")
    public R addOrUpdateVideoViewCount(@PathVariable String day){
        staService.addOrUpdateVideoViewCount(day);
        return R.ok();
    }

    @GetMapping("showData/{type}/{begin}/{end}")
    public R showData(@PathVariable String type,@PathVariable String begin,@PathVariable String end){
       Map<String,Object> map = staService.getShowData(type,begin,end);
       return R.ok().data(map);
    }

    @GetMapping("adminIndexStatistic")
    public R adminIndexStatistic(){
        AdminInfoDTO adminInfo = staService.getAdminInfo();
        return R.ok().data("adminInfo",adminInfo);
    }
}

