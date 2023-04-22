package com.example.eduservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.commonutils.model.dto.CategoryDTO;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.entity.vo.CoursePublishVo;

import java.util.List;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    public CoursePublishVo getPublishCourseInfo(String courseId);

    CourseWebVo getBaseCourseInfo(String courseId);

  List<CategoryDTO> listCategories();
}
