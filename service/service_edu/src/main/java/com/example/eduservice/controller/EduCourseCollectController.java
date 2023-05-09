package com.example.eduservice.controller;


import com.example.commonutils.R;
import com.example.eduservice.service.EduCourseCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程收藏 前端控制器
 * </p>
 *
 * @author yhy
 * @since 2023-04-22
 */
@RestController
@RequestMapping("/eduservice/collect")
public class EduCourseCollectController {

  @Autowired
  private EduCourseCollectService courseCollectService;

  @PostMapping("addCourseCollect/{courseId}/{memberId}")
  public R addCourseCollect(@PathVariable String courseId, @PathVariable String memberId) {
    Boolean success = courseCollectService.addCourseCollect(courseId, memberId);
    return success ?  R.ok() : R.error();
  }

  @PostMapping("deleteCourseCollect/{courseId}/{memberId}")
  public R deleteCourseCollect(@PathVariable String courseId, @PathVariable String memberId) {
    Boolean success = courseCollectService.deleteCourseCollect(courseId, memberId);
    return success ?  R.ok() : R.error();
  }

  @GetMapping("collectList/{memberId}")
  public R collectList(@PathVariable String memberId) {
    return R.ok().data("collectList", courseCollectService.collectList(memberId));
  }
}

