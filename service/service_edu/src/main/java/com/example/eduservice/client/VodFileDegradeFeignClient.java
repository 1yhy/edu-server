package com.example.eduservice.client;

import com.example.commonutils.R;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VodFileDegradeFeignClient implements VodClient{
    @Override
    public R removeVideo(String id) {
        return R.error().message("删除课时视频出错");
    }

    @Override
    public R deleteBatch(List<String> videoList) {
        return R.error().message("删除课程时,课时的视频删除出错");
    }

    @Override
    public R getPlayInfo(String id) {
        return R.error().message("获取课时视频信息失败");
    }
}
