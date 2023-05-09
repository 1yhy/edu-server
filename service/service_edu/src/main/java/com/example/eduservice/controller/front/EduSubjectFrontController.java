package com.example.eduservice.controller.front;

import com.example.commonutils.R;
import com.example.eduservice.entity.subject.OneSubject;
import com.example.eduservice.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eduservice/subjectfront")
public class EduSubjectFrontController {

  @Autowired
  private EduSubjectService subjectService;

  @GetMapping("getAllSubject")
  public R getAllSubject(){
    List<OneSubject> list = subjectService.getAllFrontSubject();
    return R.ok().data("list",list);
  }
}

