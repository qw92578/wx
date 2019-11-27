package com.fgq.demo.wx.controller;

import com.fgq.demo.wx.util.MessageHandlerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;


/**
 * @Package: com.fgq.demo.wx.controller
 * @ClassName: WxController
 * @Author: fgq
 * @Description: 微信服务器验证
 * @Date: 2019/11/27 11:25
 */
@RestController
public class WxController {


    @Value("${WX.TOKEN}")
    private String token;


    @RequestMapping(value="/helloWx")
    public String hello(){
        return "hello wx";
    }

    /**
     * 在微信端配置后，微信调用该接口
     * 传入参数
     * signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * timestamp	时间戳
     * nonce	    随机数
     * echostr  	随机字符串
     *
     * 接到请求后，我们需要做如下三步，若确认此次GET请求来自微信服务器，原样返回echostr参数内容，则接入生效，否则接入失败。
     *
     * 　　1. 将token、timestamp、nonce三个参数进行字典序排序
     * 　　2. 将三个参数字符串拼接成一个字符串进行sha1加密
     * 　　3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
     *
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "wx")
    public String wxcheck(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String method = request.getMethod();
        System.out.println(method);
        if(method.equals("POST")){
            return wxMessage(request,response);
        }else{
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            System.out.println(echostr);
            //1、排序
            String sortString = sort(token, timestamp, nonce);
            //2、加密
            String mySignature = sha1(sortString);
            if(signature!=null&&signature!=""&&signature.equals(mySignature)){
                System.out.println("签名校验通过。");
                return echostr;
            }else{
                System.out.println("签名校验失败.");
                return "test fail";
            }
        }

    }

    @RequestMapping(value="wxMessage",method=RequestMethod.POST)
    public String wxMessage(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        System.out.println("请求进入");
        String result = "";
        try {
            Map<String,String> map = MessageHandlerUtil.parseXml(request);
            System.out.println("开始构造消息");
            result = MessageHandlerUtil.buildXml(map);
            System.out.println(result);
            if(result.equals("")){
                result = "未正确响应";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发生异常："+ e.getMessage());
        }
        return result;
    }

    /**
     * 排序
     */
    public String sort(String token, String timestamp, String nonce){
        String[] strArray = {token,timestamp,nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for(String str : strArray){
            sb.append(str);
        }
        return sb.toString();
    }
    /**
     * 进行sha1加密
     */
    public String sha1(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }

}
