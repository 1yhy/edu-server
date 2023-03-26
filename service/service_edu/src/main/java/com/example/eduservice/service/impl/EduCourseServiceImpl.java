package com.example.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduCourseDescription;
import com.example.eduservice.entity.frontvo.CourseFrontVo;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.entity.vo.CourseInfoVo;
import com.example.eduservice.entity.vo.CoursePublishVo;
import com.example.eduservice.mapper.EduCourseMapper;
import com.example.eduservice.service.EduChapterService;
import com.example.eduservice.service.EduCourseDescriptionService;
import com.example.eduservice.service.EduCourseService;
import com.example.eduservice.service.EduVideoService;
import com.example.servicebase.exceptionhandler.EduException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;
    @Autowired
    private EduVideoService videoService;
    @Autowired
    private EduChapterService chapterService;

    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if(insert==0){
            throw new EduException(2001,"添加课程信息失败");
        }

        String cid = eduCourse.getId();

        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
        courseDescription.setId(cid);
        courseDescriptionService.save(courseDescription);
        return cid;
    }

    @Override
    public CourseInfoVo getCourseInfoById(String courseId) {
       EduCourse eduCourse = baseMapper.selectById(courseId);
       CourseInfoVo courseInfoVo =new CourseInfoVo();
       BeanUtils.copyProperties(eduCourse,courseInfoVo);
       EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
       courseInfoVo.setDescription(courseDescription.getDescription());
        return courseInfoVo;
    }

    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int update = baseMapper.updateById(eduCourse);
        if(update==0){
            throw new EduException(201,"修改课程信息失败");
        }
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfoVo.getId());
        courseDescription.setDescription(courseInfoVo.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        CoursePublishVo courseInfo = baseMapper.getPublishCourseInfo(id);
        return courseInfo;
    }

    @Override
    public void removeCourse(String courseId) {
        videoService.removeVideoByCourseId(courseId);
        chapterService.removeCourseById(courseId);
        courseDescriptionService.removeById(courseId);
        baseMapper.deleteById(courseId);
    }

    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())){
            wrapper.eq("subject_parent_id",courseFrontVo.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectId())){
            wrapper.eq("subject_id",courseFrontVo.getSubjectId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())){
            switch (courseFrontVo.getBuyCountSort()) {
                case "0":
                    wrapper.orderByDesc("buy_count");
                    break;
                case "1":
                    wrapper.orderByAsc("buy_count");
                    break;
            }
        }
        if(!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())){
            switch (courseFrontVo.getGmtCreateSort()) {
                case "0":
                    wrapper.orderByDesc("gmt_create");
                    break;
                case "1":
                    wrapper.orderByAsc("gmt_create");
                    break;
            }
        }
        if(!StringUtils.isEmpty(courseFrontVo.getPriceSort())){
            switch (courseFrontVo.getPriceSort()) {
                case "0":
                    wrapper.orderByDesc("price");
                    break;
                case "1":
                    wrapper.orderByAsc("price");
                    break;
            }
        }
        baseMapper.selectPage(pageCourse,wrapper);

        List<EduCourse> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        Map<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        return map;
    }

    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }

    @Override
    public List<EduCourse> getRelativeCourse(String courseId) {
        List<EduCourse> relativeCourseList= new ArrayList<>();
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",courseId);
        EduCourse eduCourse = baseMapper.selectOne(queryWrapper);
        List<EduCourse> courseList = baseMapper.selectList(new QueryWrapper<EduCourse>().eq("subject_id", eduCourse.getSubjectId()).ne("id",courseId));
        if(courseList.size()>4){
            for (int i = 0; i < 4; i++) {
                relativeCourseList.add(courseList.get(i));
            }
            return relativeCourseList;
        }else {
            List<EduCourse> pCourseList = baseMapper.selectList(new QueryWrapper<EduCourse>().eq("subject_parent_id", eduCourse.getSubjectParentId()).ne("id",courseId));
            List<String> collect = courseList.stream().map(item -> item.getId()).collect(Collectors.toList());
            pCourseList.stream().map(item -> {
                if(!collect.contains(item.getId())){
                    courseList.add(item);
                }
                if(courseList.size() == 4) {
                    return courseList;
                }
                return courseList;
            });

            return courseList;
        }

    }


}
