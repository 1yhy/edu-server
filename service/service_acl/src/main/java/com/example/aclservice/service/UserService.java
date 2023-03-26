package com.example.aclservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aclservice.entity.User;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 */
public interface UserService extends IService<User> {

    // 从数据库中取出用户信息
    User selectByUsername(String username);
    List<String> getAllAdmin();
}
