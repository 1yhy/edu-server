package com.example.eduservice.controller.front;

import com.example.commonutils.R;
import com.example.eduservice.entity.EduComment;
import com.example.eduservice.service.EduCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: EduCommentController
 * Package: com.example.eduservice.controller.front
 * Description:
 *
 * @Author: yhy
 * @Create: 2022/12/2 - 22:22
 * @Version: v1.0
 */
@RestController
@RequestMapping("/eduservice/commentfront")
public class EduCommentController {

    @Autowired
    private EduCommentService eduCommentService;

    @PostMapping("addComment")
    public R addComment(@Validated @RequestBody EduComment eduComment, BindingResult result){
        List<FieldError> fieldErrors = result.getFieldErrors();
        if(!fieldErrors.isEmpty()){
            return R.error().message(fieldErrors.get(0).getDefaultMessage());
        }
        return eduCommentService.saveComment(eduComment)?  R.ok() : R.error();
    }

    @GetMapping("getCommentList/{courseId}")
    public R getCommentList(@PathVariable String courseId){
        List<EduComment> commentList = eduCommentService.getCommentList(courseId);
        return R.ok().data("commentList",commentList);
    }
}
