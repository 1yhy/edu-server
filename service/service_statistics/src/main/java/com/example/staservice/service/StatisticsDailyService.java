package com.example.staservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.staservice.entity.StatisticsDaily;
import com.example.staservice.model.dto.AdminInfoDTO;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>

 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {

    // 每日访问量
    void viewCountDaily(String day);

    Map<String, Object> getShowData(String type, String begin, String end);


    void addOrUpdateRegisterCount(String day);

    void addOrUpdateLoginCount(String day);

    void addOrUpdateVideoViewCount(String day);

    AdminInfoDTO getAdminInfo();
}
