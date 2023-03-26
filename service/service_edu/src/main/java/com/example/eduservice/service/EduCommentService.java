package com.example.eduservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eduservice.entity.EduComment;

import java.util.List;

/**
 * ClassName: EduCommentService
 * Package: com.example.eduservice.service
 * Description:
 *
 * @Author: yhy
 * @Create: 2022/12/2 - 22:24
 * @Version: v1.0
 */
public interface EduCommentService extends IService<EduComment> {
    Boolean saveComment(EduComment eduComment);
    List<EduComment> getCommentList(String courseId);
//    Map<String, Object> getCommentFrontList(Page<EduComment> eduComment);
}
