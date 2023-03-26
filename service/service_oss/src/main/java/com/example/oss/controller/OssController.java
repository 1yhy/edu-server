package com.example.oss.controller;

import com.example.commonutils.R;
import com.example.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/eduoss/fileoss")
//@CrossOrigin
public class OssController {
    @Autowired
    private OssService ossService;
    @PostMapping
    public R uploadOssFile(@RequestParam("file") MultipartFile file, @RequestParam String type){
        //获取上传文件
        String url = ossService.uploadFileAvatar(file,type);
        return R.ok().data("url",url);
    }

    @DeleteMapping("delete")
    public R deleteOssFile(@RequestParam("name") String name) throws UnsupportedEncodingException {
        if(ossService.deleteOssFile(name)) {
            return R.ok();
        }
        return R.error();
    }
}
