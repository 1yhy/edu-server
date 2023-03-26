package com.example.aclservice.service.impl;

import com.example.aclservice.entity.User;
import com.example.aclservice.mapper.UserMapper;
import com.example.aclservice.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User selectByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public List<String> getAllAdmin() {
        List<User> users = baseMapper.selectList(null);
        List<String> collect = users.stream().map(user -> {
            return user.getUsername();
        }).collect(Collectors.toList());
        return collect;
    }
}
