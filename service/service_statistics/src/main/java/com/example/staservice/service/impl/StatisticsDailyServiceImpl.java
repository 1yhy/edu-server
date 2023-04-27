package com.example.staservice.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.commonutils.model.dto.CategoryDTO;
import com.example.commonutils.model.dto.LessonViewDTO;
import com.example.servicebase.service.RedisService;
import com.example.staservice.client.EduClient;
import com.example.staservice.client.OrderClient;
import com.example.staservice.client.UcenterClient;
import com.example.staservice.entity.StatisticsDaily;
import com.example.staservice.mapper.StatisticsDailyMapper;
import com.example.staservice.model.dto.AdminInfoDTO;
import com.example.staservice.model.dto.UniqueViewDTO;
import com.example.staservice.service.StatisticsDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.servicebase.constant.RedisConstant.SITE_VIEWS_COUNT;
import static com.example.servicebase.constant.RedisConstant.UNIQUE_VISITOR;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>

 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterClient ucenterClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private EduClient eduClient;

    @Autowired
    private RedisService redisService;

    @Override
    public void viewCountDaily(String day) {
        Long count = redisService.sSize(UNIQUE_VISITOR);
        LambdaQueryWrapper<StatisticsDaily> lqw =new LambdaQueryWrapper<>();
        lqw.eq(StatisticsDaily::getDateCalculated,day);
        StatisticsDaily statisticsDaily = baseMapper.selectOne(lqw);
        if(ObjectUtils.isNotNull(statisticsDaily)){
            statisticsDaily.setViewCount(Math.toIntExact(count));
            baseMapper.updateById(statisticsDaily);
        }else{
            statisticsDaily = new StatisticsDaily();
            statisticsDaily.setDateCalculated(day);
            statisticsDaily.setViewCount(Math.toIntExact(count));
            baseMapper.insert(statisticsDaily);
        }
        redisService.del(UNIQUE_VISITOR);
    }

    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        wrapper.select("date_calculated",type);

        List<StatisticsDaily> staList = baseMapper.selectList(wrapper);

        List<String> dateList= new ArrayList<>();
        List<Integer> numList = new ArrayList<>();
        for (int i = 0; i < staList.size(); i++) {
            StatisticsDaily daily = staList.get(i);
            dateList.add(daily.getDateCalculated());
            switch (type){
                case "login_num":
                    numList.add(daily.getLoginNum());
                    break;
                case "register_num":
                    numList.add(daily.getRegisterNum());
                    break;
                case "video_view_num":
                    numList.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    numList.add(daily.getCourseNum());
                    break;
                default:
                    break;
            }

        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("dateList",dateList);
        resultMap.put("numList",numList);
        return resultMap;
    }

    // 更改注册人数
    @Override
    public void addOrUpdateRegisterCount(String day) {
        LambdaQueryWrapper<StatisticsDaily> lqw =new LambdaQueryWrapper<>();
        lqw.eq(StatisticsDaily::getDateCalculated,day);
        StatisticsDaily sta = baseMapper.selectOne(lqw);
        if(sta==null){
            sta =new StatisticsDaily();
            sta.setRegisterNum(1);
            sta.setDateCalculated(day);
            baseMapper.insert(sta);
        }else{
            sta.setRegisterNum(sta.getRegisterNum()+1);
            baseMapper.update(sta,lqw);
        }
    }

    // 更改登录人数
    @Override
    public void addOrUpdateLoginCount(String day) {
        LambdaQueryWrapper<StatisticsDaily> lqw =new LambdaQueryWrapper<>();
        lqw.eq(StatisticsDaily::getDateCalculated,day);
        StatisticsDaily sta = baseMapper.selectOne(lqw);
        if(sta==null){
            sta =new StatisticsDaily();
            sta.setLoginNum(1);
            sta.setDateCalculated(day);
            baseMapper.insert(sta);
        }else{
            sta.setLoginNum(sta.getLoginNum()+1);
            baseMapper.update(sta,lqw);
        }
    }

    // 更改视频观看人数
    @Override
    public void addOrUpdateVideoViewCount(String day) {
        LambdaQueryWrapper<StatisticsDaily> lqw =new LambdaQueryWrapper<>();
        lqw.eq(StatisticsDaily::getDateCalculated,day);
        StatisticsDaily sta = baseMapper.selectOne(lqw);
        if(sta==null){
            sta =new StatisticsDaily();
            sta.setVideoViewNum(1);
            sta.setDateCalculated(day);
            baseMapper.insert(sta);
        }else{
            sta.setVideoViewNum(sta.getVideoViewNum()+1);
            baseMapper.update(sta,lqw);
        }
    }

    @Override
    public AdminInfoDTO getAdminInfo() {
        Object count = redisService.get(SITE_VIEWS_COUNT);
        Integer viewsCount = Integer.parseInt(Optional.ofNullable(count).orElse(0).toString());

        Integer userCount = ucenterClient.userCount();

        Integer orderCount = orderClient.orderCount();

        Integer lessonCount = eduClient.lessonCount();

        List<UniqueViewDTO> uniqueViews = this.listUniqueViews();

        List<CategoryDTO> categoryDTOs = eduClient.listCategories();

        List<LessonViewDTO> lessonViewDTOs = eduClient.lessonViewCount();
        AdminInfoDTO adminInfoDTO = AdminInfoDTO.builder()
                .viewsCount(viewsCount)
                .userCount(userCount)
                .orderCount(orderCount)
                .lessonCount(lessonCount)
                .uniqueViewDTOs(uniqueViews)
                .categoryDTOs(categoryDTOs)
                .lessonViewDTOs(lessonViewDTOs)
                .build();
        return adminInfoDTO;
    }

    private List<UniqueViewDTO> listUniqueViews() {
        DateTime startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -7));
        DateTime endTime = DateUtil.endOfDay(new Date());
        return baseMapper.listUniqueViews(startTime, endTime);
    }

}
