package com.example.eduservice.client;

import com.example.commonutils.R;
import org.springframework.stereotype.Component;

@Component
public class StatisticDegradeFeignClient implements StatisticClient{
  @Override
  public R addOrUpdateVideoViewCount(String day) {
    return null;
  }
}
