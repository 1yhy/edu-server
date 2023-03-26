package com.example.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eduservice.entity.EduComment;
import com.example.eduservice.mapper.EduCommentMapper;
import com.example.eduservice.service.EduCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: EduCommentServiceImpl
 * Package: com.example.eduservice.service.impl
 * Description:
 *
 * @Author: yhy
 * @Create: 2022/12/2 - 22:24
 * @Version: v1.0
 */
@Service
public class EduCommentServiceImpl extends ServiceImpl<EduCommentMapper, EduComment> implements EduCommentService {
    @Override
    public Boolean saveComment(EduComment eduComment) {
        return  baseMapper.insert(eduComment)==1;
    }

    @Override
    public List<EduComment> getCommentList(String courseId) {
        QueryWrapper<EduComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        List<EduComment> list = baseMapper.selectList(queryWrapper);
        return list;
    }
//    @Override
//    public Map<String, Object> getCommentFrontList(Page<EduComment> eduComment) {
//        return null;
//    }
}
