package com.fgq.demo.wx.service.impl;

import com.fgq.demo.wx.constant.BaseConstant;
import com.fgq.demo.wx.service.HttpClientUtilService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("httpClientUtilServiceImpl")
public class HttpClientUtilServiceImpl implements HttpClientUtilService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtilServiceImpl.class);


    @Override
    public String doGet(String url, Map<String, Object> param) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置连接超时时间  设置请求超时时间  默认允许自动重定向
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(BaseConstant.TIME_OUT_SECONDS)
                .setConnectionRequestTimeout(BaseConstant.TIME_OUT_SECONDS)
                .setSocketTimeout(BaseConstant.TIME_OUT_SECONDS)
                .setRedirectsEnabled(true)
                .build();
        if (param != null && !param.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(param.size());
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String value = entry.getValue().toString();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, BaseConstant.CHARACTER_STYLE));
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == BaseConstant.HTTP_200) {
                String resString = EntityUtils.toString(httpResponse.getEntity());//获得返回的结果
                return resString;
            }
        } catch (IOException e) {
            LOGGER.error("http get request fail : {}", e.getMessage());
        } finally {
            httpClient.close();
        }
        return null;
    }

    @Override
    public String doPost(String url, Map<String, Object> param) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(BaseConstant.TIME_OUT_SECONDS).setConnectionRequestTimeout(BaseConstant.TIME_OUT_SECONDS)
                .setSocketTimeout(BaseConstant.TIME_OUT_SECONDS).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);

        List<NameValuePair> nvps = new ArrayList<>();
        for (String key : param.keySet()) {
            nvps.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, BaseConstant.CHARACTER_STYLE));
            HttpResponse response = httpClient.execute(httpPost);
            String strResult = "";
            if (response.getStatusLine().getStatusCode() == BaseConstant.HTTP_200) {
                strResult = EntityUtils.toString(response.getEntity());
                return strResult;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("http post form request fail : {}", e.getMessage());
        } finally {
            httpClient.close();
        }
        return null;
    }

}
