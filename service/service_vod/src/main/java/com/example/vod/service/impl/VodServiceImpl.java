package com.example.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.example.servicebase.exceptionhandler.EduException;
import com.example.vod.service.VodService;
import com.example.vod.utils.ConstantVodUtils;
import com.example.vod.utils.InitVodClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VodServiceImpl implements VodService {

    @Override
    public Map<Object,Object> uploadALiYunVideo(MultipartFile file,Boolean isFree) {
        try{
            Map<Object, Object> resultMap = new HashMap<Object, Object>();
            String fileName= file.getOriginalFilename();
            String title=fileName.substring(0,fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            UploadStreamRequest request = new UploadStreamRequest(ConstantVodUtils.ACCESS_KEY_ID,ConstantVodUtils.ACCESS_KEY_SECRET, title, fileName, inputStream);
            /* 视频分类ID（可选） */
            request.setCateId(1000452927L);
            /* 模板组ID（可选）*/
            if(isFree){
                request.setTemplateGroupId("ead78ec3cf479405f0c3dd494ce0550f");
            }else {
                request.setTemplateGroupId("daf611dab8d804af04ffb1951af87cee");
            }
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            resultMap .put("RequestId", response.getRequestId());
            if (response.isSuccess()) {
                resultMap .put("videoId", response.getVideoId());
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                resultMap .put("videoId", response.getVideoId());
                resultMap .put("ErrorCode", response.getCode());
                resultMap .put("ErrorMessage=", response.getMessage());
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void removeMoreVideo(List<String> videoList) {
        try{
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID,ConstantVodUtils.ACCESS_KEY_SECRET);
            DeleteVideoRequest request= new DeleteVideoRequest();
            request.setVideoIds(StringUtils.join(videoList.toArray(),","));
            client.getAcsResponse(request);
        }catch (Exception e){
            e.printStackTrace();
            throw new EduException(201,"删除失败");
        }
    }
}
