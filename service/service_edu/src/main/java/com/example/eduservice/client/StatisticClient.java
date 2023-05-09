package com.example.eduservice.client;

import com.example.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient("service-statistic")
public interface StatisticClient {

  @PostMapping("/staservice/sta/addOrUpdateVideoViewCount/{day}")
  public R addOrUpdateVideoViewCount(@PathVariable String day);

}