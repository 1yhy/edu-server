package com.example.vod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VodService {
    Map<Object,Object> uploadALiYunVideo(MultipartFile file, Boolean isFree);

    void removeMoreVideo(List<String> videoList);
}
