package com.example.eduservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduCourseCollect;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author yhy
 * @since 2023-04-22
 */
public interface EduCourseCollectService extends IService<EduCourseCollect> {

  Boolean addCourseCollect(String courseId, String memberId);

  Boolean deleteCourseCollect(String courseId, String memberId);

  List<EduCourse> collectList(String memberId);
}
