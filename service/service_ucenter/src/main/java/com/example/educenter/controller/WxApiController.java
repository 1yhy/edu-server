package com.example.educenter.controller;

import com.example.commonutils.JwtUtils;
import com.example.educenter.client.StatisticClient;
import com.example.educenter.entity.UcenterMember;
import com.example.educenter.service.UcenterMemberService;
import com.example.educenter.utils.ConstantWxUtils;
import com.example.educenter.utils.HttpClientUtils;
import com.example.servicebase.exceptionhandler.EduException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//@CrossOrigin
@Controller//注意这里没有配置 @RestController
@RequestMapping("/educenter/api/ucenter/wx")
public class WxApiController {
    @Autowired
    private UcenterMemberService memberService;

    @Autowired
    private StatisticClient statisticClient;

    @GetMapping("login")
    public String getWxCode() {
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "#wechat_redirect";

        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(baseUrl, ConstantWxUtils.WX_OPEN_APP_ID, redirectUrl);

        return "redirect:" + url;
    }

    @GetMapping("callback")
    public String callback(@PathParam("code") String code, @PathParam("state") String state) {
        try {
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl =
                "https://api.weixin.qq.com/sns/oauth2/access_token" +
                        "?appid=%s" +
                        "&secret=%s" +
                        "&code=%s" +
                        "&grant_type=authorization_code";
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_APP_SECRET, code);


            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String accessToken = (String) mapAccessToken.get("access_token");
            String openid = (String)mapAccessToken.get("openid");

            UcenterMember member= memberService.getOpenIdMenber(openid);
            if(member==null){
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";

                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);

                String userInfo = HttpClientUtils.get(userInfoUrl);
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname");
                String headImgUrl = (String) userInfoMap.get("headimgurl");


                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headImgUrl);
                memberService.save(member);
            }

            if (member.getIsDisabled()) {
                throw new EduException(201, "您的账号已被禁用，请联系管理员了解详情");
            }

            // 登录统计人数加一
            Date date = new Date();
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
            statisticClient.addOrUpdateLoginCount(simpleFormat.format(date));

            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
            return "redirect:http://localhost:8990?token="+jwtToken;
        } catch (Exception e) {
           throw new EduException(201,"登录失败");
        }

    }


}
