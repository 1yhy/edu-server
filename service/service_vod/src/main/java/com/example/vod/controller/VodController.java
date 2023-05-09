package com.example.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.*;
import com.example.commonutils.R;
import com.example.servicebase.exceptionhandler.EduException;
import com.example.vod.service.VodService;
import com.example.vod.utils.ConstantVodUtils;
import com.example.vod.utils.InitVodClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduvod/video")
//@CrossOrigin
public class VodController {
    @Autowired
    private VodService vodService;


    @PostMapping("uploadALiYunVideo")
    public R uploadALiYunVideo(@RequestParam("file") MultipartFile file, @RequestParam Boolean isFree){
        Map<Object,Object> resultMap = vodService.uploadALiYunVideo(file,isFree);
        return R.ok().data("resultMap",resultMap);
    }

    @DeleteMapping("removeVideo/{id}")
    public R removeVideo(@PathVariable String id){
        try{
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID,ConstantVodUtils.ACCESS_KEY_SECRET);
            DeleteVideoRequest request= new DeleteVideoRequest();
            request.setVideoIds(id);
            client.getAcsResponse(request);
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            throw new EduException(201,"删除失败");
        }
    }

    @DeleteMapping("delete-batch")
    public R deleteBatch(@RequestParam("videoList") List<String> videoList){
        vodService.removeMoreVideo(videoList);
        return R.ok();
    }

    @GetMapping("getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id ){
        try{
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            request.setVideoId(id);
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);
            String playAuth = response.getPlayAuth();
            return R.ok().data("playAuth",playAuth);
        }catch (Exception e) {
            throw new EduException(201, "视频播放出错");
        }
    }

    //获取单个音视频信息
    @GetMapping("getPlayInfo/{id}")
    public R getPlayInfo(@PathVariable String id ){
        try{
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
            GetVideoInfoRequest request = new GetVideoInfoRequest();
            request.setVideoId(id);
            GetVideoInfoResponse response = client.getAcsResponse(request);
            GetVideoInfoResponse.Video videoInfo = response.getVideo();
            return R.ok().data("videoInfo",videoInfo);
        }catch (Exception e) {
            throw new EduException(201, "获取视频信息");
        }
    }

//    //    获取多个音视频信息
//    @GetMapping("getPlayInfo/{id}")
//    public R getPlayInfos(@PathVariable String ids ){
//        try{
//            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
//            GetVideoInfosRequest request = new GetVideoInfosRequest();
//            request.setVideoIds(ids);
//            GetVideoInfosResponse response = client.getAcsResponse(request);
//            List<GetVideoInfosResponse.Video> videoList = response.getVideoList();
//            return R.ok().data("videoList",videoList);
//        }catch (Exception e) {
//            throw new EduException(201, "获取视频信息");
//        }
//    }
}
