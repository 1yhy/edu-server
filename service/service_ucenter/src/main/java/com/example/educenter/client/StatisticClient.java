package com.example.educenter.client;

import com.example.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient("service-statistic")
public interface StatisticClient {

  @PostMapping("/staservice/sta/addOrUpdateRegisterCount/{day}")
  public R addOrUpdateRegisterCount(@PathVariable String day);


  @PostMapping("/staservice/sta/addOrUpdateLoginCount/{day}")
  public R addOrUpdateLoginCount(@PathVariable String day);
}