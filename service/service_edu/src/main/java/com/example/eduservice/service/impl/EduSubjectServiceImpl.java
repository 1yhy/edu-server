package com.example.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.eduservice.entity.EduCourse;
import com.example.eduservice.entity.EduSubject;
import com.example.eduservice.entity.excel.SubjectData;
import com.example.eduservice.entity.subject.OneSubject;
import com.example.eduservice.entity.subject.TwoSubject;
import com.example.eduservice.listener.SubjectExcelListener;
import com.example.eduservice.mapper.EduSubjectMapper;
import com.example.eduservice.service.EduCourseService;
import com.example.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Autowired
    private EduCourseService eduCourseService;
    @Override
    public void saveSubject(MultipartFile file,EduSubjectService subjectService) {
        try{
            InputStream in = file.getInputStream();
            EasyExcel.read(in, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<OneSubject> getAllSubject() {
       //查询所有有课程的一级和二级分类id
        QueryWrapper<EduCourse> oneSubjectWrapper = new QueryWrapper<>();
        oneSubjectWrapper.select("subject_parent_id");
        List<String> oneSubjectIds = eduCourseService.list(oneSubjectWrapper).stream().map(EduCourse::getSubjectParentId).distinct().collect(Collectors.toList());


        QueryWrapper<EduCourse> twoSubjectWrapper = new QueryWrapper<>();
        twoSubjectWrapper.select("subject_id");
        List<String> twoSubjectIds = eduCourseService.list(twoSubjectWrapper).stream().map(EduCourse::getSubjectId).distinct().collect(Collectors.toList());

        System.err.println(oneSubjectIds);
        System.err.println(twoSubjectIds);
        //查询所有有课程的一级和二级分类
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.in("id",oneSubjectIds);
        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);

        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.in("id", twoSubjectIds).ne("parent_id","0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);

        //最终集合
        List<OneSubject> finalSubjectList = new ArrayList<>();

        for (int i = 0; i < oneSubjectList.size(); i++) {
            EduSubject eduSubject = oneSubjectList.get(i);
            OneSubject oneSubject = new OneSubject();
//            oneSubject.setId(eduSubject.getId());
//            oneSubject.setTitle(eduSubject.getTitle());
            BeanUtils.copyProperties(eduSubject,oneSubject);
            finalSubjectList.add(oneSubject);

            List<TwoSubject> twoFinalSubjectList = new ArrayList<>();
            for (int m = 0; m < twoSubjectList.size(); m++) {
                EduSubject tSubject = twoSubjectList.get(m);
                if(tSubject.getParentId().equals(eduSubject.getId())){
                    TwoSubject twoSubject=new TwoSubject();
                    BeanUtils.copyProperties(tSubject,twoSubject);
                    twoFinalSubjectList.add(twoSubject);
                }
            }
            oneSubject.setChildren(twoFinalSubjectList);
        }

        return finalSubjectList;
    }
}
