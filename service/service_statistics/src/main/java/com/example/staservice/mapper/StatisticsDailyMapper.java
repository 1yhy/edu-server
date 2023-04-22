package com.example.staservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.staservice.entity.StatisticsDaily;
import com.example.staservice.model.dto.UniqueViewDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 网站统计日数据 Mapper 接口
 * </p>

 */
public interface StatisticsDailyMapper extends BaseMapper<StatisticsDaily> {

  List<UniqueViewDTO> listUniqueViews(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
