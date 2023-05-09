package com.example.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduCourseCollect;
import com.example.eduservice.mapper.EduCourseCollectMapper;
import com.example.eduservice.service.EduCourseCollectService;
import com.example.eduservice.service.EduCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author yhy
 * @since 2023-04-22
 */
@Service
public class EduCourseCollectServiceImpl extends ServiceImpl<EduCourseCollectMapper, EduCourseCollect> implements EduCourseCollectService {


  @Autowired
  private EduCourseService courseService;
  @Override
  public Boolean addCourseCollect(String courseId, String memberId) {
    QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
    wrapper.eq("course_id", courseId);
    wrapper.eq("member_id", memberId);
    Integer count = baseMapper.selectCount(wrapper);
    if (count > 0) {
      throw new RuntimeException("已经收藏过了");
    }
    EduCourseCollect courseCollect = new EduCourseCollect();
    courseCollect.setCourseId(courseId);
    courseCollect.setMemberId(memberId);
    return baseMapper.insert(courseCollect) == 1;
  }

  @Override
  public Boolean deleteCourseCollect(String courseId, String memberId) {
    QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
    wrapper.eq("course_id", courseId);
    wrapper.eq("member_id", memberId);
    return baseMapper.delete(wrapper) == 1;
  }

  @Override
  public List<EduCourse> collectList(String memberId) {
    QueryWrapper<EduCourseCollect> wrapper = new QueryWrapper<>();
    wrapper.eq("member_id", memberId).select("course_id"); // 只查询course_id
    // 查询出所有的course_id
    List<String> collect = baseMapper.selectList(wrapper).stream().map(EduCourseCollect::getCourseId).collect(Collectors.toList());

    // 根据course_id查询课程
    if (collect.size() > 0) {
      return new ArrayList<>(courseService.listByIds(collect));
    }
    return null;
  }
}
