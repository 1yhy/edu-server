package com.example.staservice.client;

import com.example.commonutils.model.dto.CategoryDTO;
import com.example.commonutils.model.dto.LessonViewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Component
@FeignClient(name = "service-edu", contextId="service-edu-statistics")
public interface EduClient {
    @GetMapping("/eduservice/course/lessonCount")
    Integer lessonCount();

    @GetMapping("/eduservice/course/listCategories")
    List<CategoryDTO> listCategories();

    @GetMapping("/eduservice/course/lessonViewCount")
    List<LessonViewDTO> lessonViewCount();
}
