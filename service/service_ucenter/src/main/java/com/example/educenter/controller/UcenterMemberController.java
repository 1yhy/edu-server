package com.example.educenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commonutils.JwtUtils;
import com.example.commonutils.R;
import com.example.commonutils.ordervo.UcenterMemberOrder;
import com.example.educenter.entity.UcenterMember;
import com.example.educenter.entity.vo.RegisterVo;
import com.example.educenter.entity.vo.UserQuery;
import com.example.educenter.service.UcenterMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping("/educenter/member")
//@CrossOrigin
public class UcenterMemberController {
    @Autowired
    private UcenterMemberService memberService;

    @PostMapping("login")
    public R loginUser(@RequestBody UcenterMember member) {
        String token = memberService.login(member);
        return R.ok().data("token", token);
    }

    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        if(memberService.register(registerVo)){
            return R.ok();
        }else {
        return  R.error();
        }
    }



    @GetMapping("getUserInfo")
    public R getUserInfo(HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        UcenterMember member = memberService.getById(memberId);
        return R.ok().data("userInfo",member);
    }

    @PostMapping("getUserInfoOrder/{id}")
    public UcenterMemberOrder getUserInfoOrder(@PathVariable String id){
        UcenterMember member = memberService.getById(id);
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member,ucenterMemberOrder);
        return ucenterMemberOrder;
    }

    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day){
        Integer count = memberService.countRegister(day);
        return R.ok().data("countRegister",count);
    }

    @GetMapping("userCount")
    public Integer userCount(){
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted",0);
        return memberService.count(wrapper);
    }

    @PostMapping("update")
    public R updateUserInfo(@RequestBody UcenterMember member){
        boolean success = memberService.updateInfo(member);
        if(success){
            UcenterMember ucenterMember = memberService.getById(member.getId());
            return R.ok().data("userInfo",ucenterMember);
        }
        return R.error();
    }

    //条件查询带分页的方法
    @PostMapping("pageUserCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) UserQuery userQuery) {
        Page<UcenterMember> pageUser = new Page<>(current, limit);
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        //多条件组合查询
        String nickname = userQuery.getNickname();
        String begin = userQuery.getBegin();
        String end = userQuery.getEnd();
        Integer sex = userQuery.getSex();
        //判断条件值是否为空，如果不为空拼接条件
        if (!StringUtils.isEmpty(nickname)) {
            wrapper.like("nickname", nickname);
        }
        if (!StringUtils.isEmpty(sex)) {
            wrapper.eq("sex", sex);
        }
        if (!StringUtils.isEmpty(begin)) {
            wrapper.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            wrapper.le("gmt_create", end);
        }

        wrapper.orderByDesc("gmt_create");
        memberService.page(pageUser, wrapper);
        long total = pageUser.getTotal();
        List<UcenterMember> records = pageUser.getRecords();
        return R.ok().data("total", total).data("rows", records);
    }


    @DeleteMapping("deleteUser/{id}")
    public R deleteUser(@PathVariable String id){
        LambdaQueryWrapper<UcenterMember> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(UcenterMember::getId,id);
        if(ObjectUtils.isEmpty(memberService.getOne(wrapper))){
            return R.error().message("用户不存在");
        }
        boolean remove = memberService.remove(wrapper);
        return remove?R.ok():R.error();
    }

}

