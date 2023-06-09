package com.example.eduservice.client;

import com.example.servicebase.exceptionhandler.EduException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderFileDegradeFeignClient implements OrderClient{
    @Override
    public boolean isBuy(String courseId, String memberId) {
        throw new EduException(201,"查询购买课程情况出错");
    }

    @Override
    public List<String> getBoughtCourse(String memberId) {
        throw new EduException(201,"查询购买课程情况出错");
    }
}
