package com.example.eduservice.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commonutils.JwtUtils;
import com.example.commonutils.R;
import com.example.commonutils.ordervo.CourseWebVoOrder;
import com.example.eduservice.client.OrderClient;
import com.example.eduservice.client.StatisticClient;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduVideo;
import com.example.eduservice.entity.chapter.ChapterVo;
import com.example.eduservice.entity.frontvo.CourseFrontVo;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.service.EduChapterService;
import com.example.eduservice.service.EduCourseService;
import com.example.eduservice.service.EduVideoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Date;
/**
 * The type Course front controller.
 */
@RestController
@RequestMapping("/eduservice/coursefront")
//@CrossOrigin
public class CourseFrontController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduVideoService eduVideoService;

    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private OrderClient orderClient;

    @Autowired
    private StatisticClient statisticClient;

    /**
     * Gets front course list.
     *
     * @param page          the page
     * @param limit         the limit
     * @param courseFrontVo the course front vo
     * @return the front course list
     */
    @PostMapping("getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable long page, @PathVariable long limit,
                                @RequestBody(required = false) CourseFrontVo courseFrontVo){
        Page<EduCourse> pageCourse = new Page<>(page,limit);
       Map<String,Object> map =  courseService.getCourseFrontList(pageCourse,courseFrontVo);
        return R.ok().data(map);
    }


    /**
     * Gets front course list.
     *
     * @param page          the page
     * @param limit         the limit
     * @param keyword       the search keyword
     * @return the front course list
     */
    @PostMapping("searchCourse/{page}/{limit}/{keyword}")
    public R searchCourse(@PathVariable long page, @PathVariable long limit,
                          @PathVariable String keyword){
        Page<EduCourse> pageCourse = new Page<>(page,limit);
        Map<String,Object> map =  courseService.searchCourse(pageCourse,keyword);
        return R.ok().data(map);
    }
    /**
     * Get front course info r.
     *
     * @param courseId the course id
     * @param request  the request
     * @return the r
     */
    @GetMapping("getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request){
       // 课程浏览数量统计
        EduCourse eduCourse = courseService.getById(courseId);
        eduCourse.setViewCount(eduCourse.getViewCount()+1);
        courseService.updateById(eduCourse);

        // 课程信息实体
        CourseWebVo courseWebVo= courseService.getBaseCourseInfo(courseId);
        List<ChapterVo> chapterVoList = chapterService.getChapterVideoByCourseId(courseId);
        boolean isBuy = false;
        if(!StringUtils.isEmpty(JwtUtils.getMemberIdByJwtToken(request))){
            isBuy = orderClient.isBuy(courseId, JwtUtils.getMemberIdByJwtToken(request));
        }
        return R.ok().data("courseWebVo",courseWebVo).data("chapterVoList",chapterVoList).data("isBuy",isBuy);
    }

    /**
     * Get course info order course web vo order.
     *
     * @param id the id
     * @return the course web vo order
     */
    @PostMapping("getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable String id){
        CourseWebVo courseInfo = courseService.getBaseCourseInfo(id);
        CourseWebVoOrder courseWebVoOrder = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfo, courseWebVoOrder);
        return courseWebVoOrder;
    }

    /**
     * Gets playing video info.
     *
     * @param id the id
     * @return the playing video info
     */
    @GetMapping("getPlayingVideoInfo/{id}")
    public R getPlayingVideoInfo(@PathVariable String id) {
        LambdaQueryWrapper<EduVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduVideo::getVideoSourceId,id);
        EduVideo video = eduVideoService.getOne(wrapper);
        // 播放次数加一
        video.setPlayCount(video.getPlayCount()+1);
        eduVideoService.updateById(video);
        // 统计视频播放量加一
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(date);
        statisticClient.addOrUpdateVideoViewCount(day);
        return R.ok().data("video",video);
    }

    @GetMapping("getRelativeCourse/{courseId}")
    public R getRelativeCourse(@PathVariable String courseId){
        List<EduCourse> relativeCourse = courseService.getRelativeCourse(courseId);
        System.err.println(relativeCourse);
        return R.ok().data("relativeCourse",relativeCourse);
    }

    // 更新课程购买数量
    @PostMapping("updateCourseBuyCount/{id}")
    public R updateCourseBuyCount(@PathVariable String id){
        EduCourse eduCourse = courseService.getById(id);
        eduCourse.setBuyCount(eduCourse.getBuyCount()+1);
        courseService.updateById(eduCourse);
        return R.ok();
    }

    @GetMapping("getBoughtCourseList/{userId}")
    public R getBoughtCourseList(@PathVariable String userId){
        List<EduCourse> boughtCourseList = courseService.getBoughtCourseList(userId);
        return R.ok().data("courseList",boughtCourseList);
    }
}
