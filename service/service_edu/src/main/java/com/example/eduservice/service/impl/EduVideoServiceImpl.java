package com.example.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.eduservice.client.VodClient;
import com.example.eduservice.entity.EduVideo;
import com.example.eduservice.mapper.EduVideoMapper;
import com.example.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    @Autowired
    private VodClient vodClient;

    @Override
    public void removeVideoByCourseId(String courseId) {
        LambdaQueryWrapper<EduVideo> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(EduVideo::getCourseId, courseId);
        lqw2.select(EduVideo::getVideoSourceId);
        List<EduVideo> eduVideoList = baseMapper.selectList(lqw2);
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < eduVideoList.size(); i++) {
            if (!StringUtils.isEmpty(eduVideoList.get(i).getVideoSourceId())) {
                videoIds.add(eduVideoList.get(i).getVideoSourceId());
            }
        }
        if (videoIds.size() > 0) vodClient.deleteBatch(videoIds);

        LambdaQueryWrapper<EduVideo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(EduVideo::getCourseId, courseId);
        baseMapper.delete(lqw);
    }
}
