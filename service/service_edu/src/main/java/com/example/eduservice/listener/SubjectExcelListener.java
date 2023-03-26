package com.example.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.eduservice.entity.EduSubject;
import com.example.eduservice.entity.excel.SubjectData;
import com.example.eduservice.service.EduSubjectService;
import com.example.servicebase.exceptionhandler.EduException;

public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
    public EduSubjectService subjectService;

    public SubjectExcelListener(){}

    public SubjectExcelListener(EduSubjectService subjectService){
        this.subjectService=subjectService;
    }

    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if(subjectData == null){
            throw new EduException(201,"文件数据为空");
        }

        EduSubject existOneSubject = this.existOneSubject(subjectService,subjectData.getOneSubjectName());
        if(existOneSubject==null){
            existOneSubject=new EduSubject();
            existOneSubject.setParentId("0");
            existOneSubject.setTitle(subjectData.getOneSubjectName());
            subjectService.save(existOneSubject);
        }
        String pid=existOneSubject.getId();
        EduSubject existTwoSubject = this.existTwoSubject(subjectService,subjectData.getOneSubjectName(),pid);
        if(existTwoSubject==null){
            existTwoSubject=new EduSubject();
            existTwoSubject.setParentId(pid);
            existTwoSubject.setTitle(subjectData.getTwoSubjectName());
            subjectService.save(existTwoSubject);
        }
    }

    private EduSubject existOneSubject(EduSubjectService subjectService,String name){
        LambdaQueryWrapper<EduSubject> lqw=new LambdaQueryWrapper<>();
        lqw.eq(EduSubject::getTitle,name).eq(EduSubject::getParentId,"0");
        EduSubject oneSubject = subjectService.getOne(lqw);
        return oneSubject;
    }

    private EduSubject existTwoSubject(EduSubjectService subjectService,String name,String pid){
        LambdaQueryWrapper<EduSubject> lqw=new LambdaQueryWrapper<>();
        lqw.eq(EduSubject::getTitle,name).eq(EduSubject::getParentId,pid);
        EduSubject oneSubject = subjectService.getOne(lqw);
        return oneSubject;
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
