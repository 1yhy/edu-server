package com.example.educenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.example"})
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication//取消数据源自动配置
@MapperScan("com.example.educenter.mapper")
public class UcenterApplication {
     public static void main(String[] args) {
        SpringApplication.run(UcenterApplication.class, args);
    }

}
