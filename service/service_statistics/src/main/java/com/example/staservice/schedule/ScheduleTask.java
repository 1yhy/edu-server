package com.example.staservice.schedule;

import com.example.staservice.service.StatisticsDailyService;
import com.example.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleTask {
    @Autowired
    private StatisticsDailyService service;

    @Scheduled(cron = "0 0 1 * * ?")
    public void task() {
        service.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(), -1)));
    }
}
