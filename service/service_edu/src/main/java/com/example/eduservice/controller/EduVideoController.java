package com.example.eduservice.controller;


import com.example.commonutils.R;
import com.example.eduservice.client.VodClient;
import com.example.eduservice.entity.EduVideo;
import com.example.eduservice.service.EduVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/eduservice/video")
public class EduVideoController {
    @Autowired
    private EduVideoService videoService;
    @Autowired
    private VodClient vodClient;

    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo) {
        R playInfo = vodClient.getPlayInfo(eduVideo.getVideoSourceId());
        Map<Object, Object> videoInfo = (Map<Object, Object>) playInfo.getData().get("videoInfo");
        eduVideo.setDuration(Float.valueOf(videoInfo.get("duration").toString()));
        eduVideo.setStatus(videoInfo.get("status").toString());
        eduVideo.setSize(Long.valueOf(videoInfo.get("size").toString()));
        videoService.save(eduVideo);
        return R.ok();
    }

    @GetMapping("getVideoInfo/{id}")
    public R getVideoInfo(@PathVariable String id) {
        EduVideo video = videoService.getById(id);
        return R.ok().data("video", video);
    }

    @DeleteMapping("{id}")
    public R deleteVideo(@PathVariable String id) {
        EduVideo eduVideo = videoService.getById(id);

        if (!StringUtils.isEmpty(eduVideo.getVideoSourceId())) {
            vodClient.removeVideo(eduVideo.getVideoSourceId());
        }
        videoService.removeById(id);
        return R.ok();
    }

    @PostMapping("updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo) {
        videoService.updateById(eduVideo);
        return R.ok();
    }
}

