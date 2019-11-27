package com.fgq.demo.wx.service;

import java.io.IOException;
import java.util.Map;

public interface HttpClientUtilService {

    /**
     * httpClient get 请求
     * @param url
     * @param param
     * @return
     * @throws IOException
     */
    String doGet(String url, Map<String, Object> param) throws IOException;

    /**
     * httpClient post form 请求
     * @param url
     * @param param
     * @return
     * @throws IOException
     */
    String doPost(String url, Map<String, Object> param) throws IOException;
}
