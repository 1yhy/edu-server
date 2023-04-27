package com.example.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commonutils.R;
import com.example.commonutils.model.dto.CategoryDTO;
import com.example.commonutils.model.dto.LessonViewDTO;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.vo.CourseInfoVo;
import com.example.eduservice.entity.vo.CoursePublishVo;
import com.example.eduservice.entity.vo.CourseQuery;
import com.example.eduservice.service.EduCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/eduservice/course")
//@CrossOrigin
public class EduCourseController {
    @Autowired
    private EduCourseService courseService;

    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R pageCourseCondition(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) CourseQuery courseQuery) {
        Page<EduCourse> pageCourse = new Page<>(current, limit);
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //多条件组合查询
        String title = courseQuery.getTitle();
        String status = courseQuery.getStatus();
        //判断条件值是否为空，如果不为空拼接条件
        if (!StringUtils.isEmpty(title)) {
            wrapper.like("title", title);
        }
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("gmt_create");
        courseService.page(pageCourse, wrapper);
        long total = pageCourse.getTotal();
        List<EduCourse> records = pageCourse.getRecords();
        return R.ok().data("total", total).data("rows", records);
    }


    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo) {
        String id = courseService.saveCourseInfo(courseInfoVo);
        return R.ok().data("courseId",id);
    }

    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId){
        CourseInfoVo courseInfoVo = courseService.getCourseInfoById(courseId);

        return R.ok().data("courseInfoVo",courseInfoVo);
    }

    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    @GetMapping("getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id){
        CoursePublishVo publishVo = courseService.publishCourseInfo(id);
        return R.ok().data("publishCourse",publishVo);
    }

    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);
        return R.ok();
    }

    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.removeCourse(courseId);
        return R.ok();
    }

    @GetMapping("lessonCount")
    public Integer lessonCount(){
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status","Normal");
        return courseService.count(wrapper);
    }

    // 课程分类列表
    @GetMapping("listCategories")
    public List<CategoryDTO> listCategories() {
        return courseService.listCategories();
    }

    // 课程浏览量统计
    @GetMapping("lessonViewCount")
    public List<LessonViewDTO> lessonViewCount() {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("status","Normal").select("id","title","view_count").orderByDesc("view_count").last("limit 8");
        List<LessonViewDTO> lessonViewCountDTOS = courseService.list(wrapper).stream().map(course -> {
            LessonViewDTO lessonViewCountDTO = new LessonViewDTO();
            lessonViewCountDTO.setId(course.getId());
            lessonViewCountDTO.setName(course.getTitle());
            lessonViewCountDTO.setViewsCount(Math.toIntExact(course.getViewCount()));
            return lessonViewCountDTO;
        }).collect(Collectors.toList());
        return lessonViewCountDTOS;
    }
}

