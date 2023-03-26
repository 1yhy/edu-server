package com.example.educenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.commonutils.JwtUtils;
import com.example.educenter.entity.UcenterMember;
import com.example.educenter.entity.vo.RegisterVo;
import com.example.educenter.mapper.UcenterMemberMapper;
import com.example.educenter.service.UcenterMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.servicebase.exceptionhandler.EduException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-07-04
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String, String> template;

    @Override
    public String login(UcenterMember member) {
        String mobile = member.getMobile();
        String password = member.getPassword();

        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new EduException(201, "登录失败");
        }

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        UcenterMember mobileMember = baseMapper.selectOne(wrapper);
        if (mobileMember == null) {
            throw new EduException(201, "登录失败");
        }

        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(mobileMember.getPassword())) {
            System.out.println(DigestUtils.md5DigestAsHex(password.getBytes()));
            System.out.println(mobileMember.getPassword());
            System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));
            throw new EduException(201, "登录失败");
        }

        if (mobileMember.getIsDisabled()) {
            throw new EduException(201, "登录失败");
        }

        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        return jwtToken;
    }

    @Override
    public boolean register(RegisterVo registerVo) {
        String code = registerVo.getCode();
        String mobile = registerVo.getMobile();
            String nickname = registerVo.getNickname();
        String password = registerVo.getPassword();

        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code) || StringUtils.isEmpty(nickname)) {
            throw new EduException(201, "注册失败");
        }

        String redisCode = template.opsForValue().get(mobile);
        if (!code.equals(redisCode)) {
            throw new EduException(201, "注册失败");
        }

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new EduException(201, "注册失败");
        }
            UcenterMember member = new UcenterMember();
            member.setMobile(mobile);
            member.setNickname(nickname);
            member.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
            member.setIsDisabled(false);
            member.setAvatar("https://img1.baidu.com/it/u=2400787025,2443994425&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
            return baseMapper.insert(member) == 1;
        }

    @Override
    public UcenterMember getOpenIdMenber(String openid) {
        QueryWrapper<UcenterMember> wrapper =new QueryWrapper<>();
        wrapper.eq("openid",openid);
        UcenterMember member = baseMapper.selectOne(wrapper);
        return member;
    }

    @Override
    public Integer countRegister(String day) {
        return baseMapper.countRegister(day);
    }


}
