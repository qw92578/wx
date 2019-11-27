package com.fgq.demo.wx.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fgq.demo.wx.dto.AccessTokenDTO;
import com.fgq.demo.wx.service.HttpClientUtilService;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.fgq.demo.wx.timer
 * @ClassName: GetWXAccessTokenTask
 * @Author: fgq
 * @Description: 定时获取微信的accesstoken
 * @Date: 2019/11/27 15:46
 */
@Component
@EnableScheduling
public class GetWXAccessTokenTask {

    private static Map<String,String> accessTokenMap = new HashMap<>();

    @Value("${WX.appID}")
    private String appID;

    @Value("${WX.appsecret}")
    private String appSecret;

    @Value("${WX.token.url}")
    private String accessUrl;

    @Autowired
    HttpClientUtilService httpClientUtilService;

    @Scheduled(cron = "0 0 0/1 * * ? ")
    private void updateAccessToken() {
        System.err.println("开始执行定时任务"+httpClientUtilService+accessUrl);
        try {
            String s = httpClientUtilService.doGet(accessUrl + "&appid=" + appID + "&secret=" + appSecret, null);
            JSONObject jsonObject = JSON.parseObject(s);
            String accessToken = jsonObject.getString("access_token");
            accessTokenMap.put("accecc_token",accessToken);
            //TODO:入库操作
            AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
            accessTokenDTO.setAccessToken(accessToken);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("定时任务出错");
        }
        System.err.println("accessTokenMap:"+accessTokenMap);
    }

}
