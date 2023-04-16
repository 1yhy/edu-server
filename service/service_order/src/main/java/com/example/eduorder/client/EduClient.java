package com.example.eduorder.client;

import com.example.commonutils.R;
import com.example.commonutils.ordervo.CourseWebVoOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient("service-edu")
public interface EduClient {

    @PostMapping("/eduservice/coursefront/getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable("id") String id);

    // 更新课程购买数量
    @PostMapping("/eduservice/coursefront/updateCourseBuyCount/{id}")
    public R updateCourseBuyCount(@PathVariable String id);
}
