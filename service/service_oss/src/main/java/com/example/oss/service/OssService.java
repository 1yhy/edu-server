package com.example.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    //上传头像到Oss
    String uploadFileAvatar(MultipartFile file,String type);

    Boolean deleteOssFile(String name);
}
