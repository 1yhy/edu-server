package com.example.vod.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class ConstantVodUtils implements InitializingBean {
    //读取配置文件内容
    @Value("${aliyun.vod.file.keyid}")
    private String keyId;
    @Value("${aliyun.vod.file.keysecret}")
    private String keySecret;

    @Value("${aliyun.vod.file.regionid}")
    private String regionId;

    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;

    public static String REGION_ID;

    @Override
    public void afterPropertiesSet() throws Exception {
        ACCESS_KEY_ID = keyId;
        ACCESS_KEY_SECRET = keySecret;
        REGION_ID = regionId;

    }
}
