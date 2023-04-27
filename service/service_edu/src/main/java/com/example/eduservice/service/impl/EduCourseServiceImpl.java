package com.example.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.commonutils.model.dto.CategoryDTO;
import com.example.eduservice.client.OrderClient;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduCourseDescription;
import com.example.eduservice.entity.EduSubject;
import com.example.eduservice.entity.EduTeacher;
import com.example.eduservice.entity.frontvo.CourseFrontVo;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.entity.vo.CourseInfoVo;
import com.example.eduservice.entity.vo.CoursePublishVo;
import com.example.eduservice.mapper.EduCourseMapper;
import com.example.eduservice.service.*;
import com.example.eduservice.utils.IpUtil;
import com.example.servicebase.exceptionhandler.EduException;
import com.example.servicebase.service.RedisService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.servicebase.constant.CommonConstant.UNKNOWN;
import static com.example.servicebase.constant.RedisConstant.*;

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
    private EduSubjectService subjectService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderClient orderClient;

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
        wrapper.eq("status","Normal");
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
            for (int i = 0; i < pCourseList.size(); i++) {
                if(collect.size() == 0 || !collect.contains(pCourseList.get(i).getId())){
                    courseList.add(pCourseList.get(i));
                }
                if(courseList.size() > 4){
                    break;
                }
            }
            return courseList;
        }

    }

    @Override
    public void report() {
        String ipAddress = IpUtil.getIpAddress(request);
        UserAgent userAgent = IpUtil.getUserAgent(request);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        String uuid = ipAddress + browser.getName() + operatingSystem.getName();
        String md5 = DigestUtils.md5DigestAsHex(uuid.getBytes());
        if (!redisService.sIsMember(UNIQUE_VISITOR, md5)) {
            String ipSource = IpUtil.getIpSource(ipAddress);
            if (StringUtils.isNotEmpty(ipSource)) {
                String ipProvince = IpUtil.getIpProvince(ipSource);
                redisService.hIncr(VISITOR_AREA, ipProvince, 1L);
            } else {
                redisService.hIncr(VISITOR_AREA, UNKNOWN, 1L);
            }
            redisService.incr(SITE_VIEWS_COUNT, 1);
            redisService.sAdd(UNIQUE_VISITOR, md5);
        }
    }

    @Override
    public List<CategoryDTO> listCategories() {
        return baseMapper.listCategories();
//        return categoryDTOS;
    }

    @Override
    public List<EduCourse> getBoughtCourseList(String userId) {
        List<String> boughtCourse = orderClient.getBoughtCourse(userId);
        if(boughtCourse.size() > 0){
            List<EduCourse> eduCourseList = baseMapper.selectBatchIds(boughtCourse);
            return eduCourseList;
        }
        return null;
    }


    // 搜索课程
    @Override
    public Map<String, Object> searchCourse(Page<EduCourse> pageCourse, String keyword) {
        QueryWrapper<EduSubject> subjectQueryWrapper = new QueryWrapper<>();
        subjectQueryWrapper.like("title",keyword).select("id");
        List<String> collect = subjectService.list(subjectQueryWrapper).stream().map(item -> item.getId()).collect(Collectors.toList());


        QueryWrapper<EduTeacher> teacherQueryWrapper = new QueryWrapper<>();
        teacherQueryWrapper.like("name",keyword).select("id");
        List<String> teacherList = teacherService.list(teacherQueryWrapper).stream().map(item -> item.getId()).collect(Collectors.toList());

        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.likeRight("title",keyword);
        if (teacherList.size() > 0){
            wrapper.or().in("teacher_id",teacherList);
        }
        if (collect.size() > 0){
            wrapper.or().in("subject_id",collect).or().in("subject_parent_id",collect);
        }
        wrapper.eq("status","Normal");
        baseMapper.selectPage(pageCourse,wrapper);

        List<EduCourse> records = pageCourse.getRecords();
        List<CourseWebVo> courseWebVoList = new ArrayList<>();
        for (EduCourse record : records) {
            CourseWebVo courseWebVo = new CourseWebVo();
            BeanUtils.copyProperties(record, courseWebVo);
            courseWebVo.setTeacherName(teacherService.getById(record.getTeacherId()).getName());

            // 课程描述是否为空
            EduCourseDescription courseDescriptionServiceById = courseDescriptionService.getById(record.getId());
            if(courseDescriptionServiceById != null){
                courseWebVo.setDescription(courseDescriptionServiceById.getDescription());
            }
            courseWebVoList.add(courseWebVo);
        }
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        Map<String, Object> map = new HashMap<>();
        map.put("items", courseWebVoList);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        return map;
    }

}
