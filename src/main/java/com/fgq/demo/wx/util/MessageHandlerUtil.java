package com.fgq.demo.wx.util;

//import com.bean.wx.resp.*;

import com.alibaba.fastjson.JSON;
import com.fgq.demo.wx.resp.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 消息处理工具类
 */
public class MessageHandlerUtil {
    // 请求消息类型：文本
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    // 请求消息类型：图片
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    // 请求消息类型：语音
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
    // 请求消息类型：视频
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
    // 请求消息类型：地理位置
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
    // 请求消息类型：链接
    public static final String REQ_MESSAGE_TYPE_LINK = "link";

    // 请求消息类型：事件推送
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    // 事件类型：subscribe(订阅)
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    // 事件类型：unsubscribe(取消订阅)
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    // 事件类型：scan(用户已关注时的扫描带参数二维码)
    public static final String EVENT_TYPE_SCAN = "scan";
    // 事件类型：LOCATION(上报地理位置)
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
    // 事件类型：CLICK(自定义菜单)
    public static final String EVENT_TYPE_CLICK = "CLICK";
    // View
    public static final String EVENT_TYPE_VIEW = "VIEW";

    // 响应消息类型：文本
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    // 响应消息类型：图片
    public static final String RESP_MESSAGE_TYPE_IMAGE = "image";
    // 响应消息类型：语音
    public static final String RESP_MESSAGE_TYPE_VOICE = "voice";
    // 响应消息类型：视频
    public static final String RESP_MESSAGE_TYPE_VIDEO = "video";
    // 响应消息类型：音乐
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
    // 响应消息类型：图文
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";
    // 响应消息类型：消息转发到多客服
    public static final String RESP_MESSAGE_TYPE_TRANSFER_CUSTOMER_SERVICE = "transfer_customer_service";

    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return Map<String   ,       String>
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        //SAXBuilder builder = new SAXBuilder();

        //解决微信支付xml解析漏洞
        String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        reader.setFeature(FEATURE, true);

        FEATURE = "http://xml.org/sax/features/external-general-entities";
        reader.setFeature(FEATURE, false);

        FEATURE = "http://xml.org/sax/features/external-parameter-entities";
        reader.setFeature(FEATURE, false);

        FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        reader.setFeature(FEATURE, false);

        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * 扩展xstream使其支持CDATA
     */
    private static XStream xstream = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                @Override
                public void startNode(String name, @SuppressWarnings("rawtypes") Class clazz) {
                    super.startNode(name, clazz);
                }

                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String messageToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }

    /**
     * 图片消息对象转换成xml
     *
     * @param imageMessage 图片消息对象
     * @return xml
     */
    public static String messageToXml(ImageMessage imageMessage) {
        xstream.alias("xml", imageMessage.getClass());
        return xstream.toXML(imageMessage);
    }

    /**
     * 语音消息对象转换成xml
     *
     * @param voiceMessage 语音消息对象
     * @return xml
     */
    public static String messageToXml(VoiceMessage voiceMessage) {
        xstream.alias("xml", voiceMessage.getClass());
        return xstream.toXML(voiceMessage);
    }

    /**
     * 视频消息对象转换成xml
     *
     * @param videoMessage 视频消息对象
     * @return xml
     */
    public static String messageToXml(VideoMessage videoMessage) {
        xstream.alias("xml", videoMessage.getClass());
        return xstream.toXML(videoMessage);
    }

    /**
     * 音乐消息对象转换成xml
     *
     * @param musicMessage 音乐消息对象
     * @return xml
     */
    public static String messageToXml(MusicMessage musicMessage) {
        xstream.alias("xml", musicMessage.getClass());
        return xstream.toXML(musicMessage);
    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @return xml
     */
    public static String messageToXml(NewsMessage newsMessage) {
        xstream.alias("xml", newsMessage.getClass());
        xstream.alias("item", new Article().getClass());
        return xstream.toXML(newsMessage);
    }

    /*
     * 电话号码
     */
    private static final String PURPLE_TEL_NUMBER = "xxxx";
    private static final String SUZI_TEL_NUMBER = "xxxxx";

    /*
     * 特权码
     */
    private static final String LINSHI = "1314520001";
    private static final String LATIAO = "1314520002";
    private static final String SONG = "1314520003";
    private static final String GOLD = "1314520004";
    private static final String WHITE_GOLD = "1314520005";
    private static final String COLD_CLEAN = "1314520006";
    private static final String DIAMOND = "1314520";
    private static final String LIWU = "礼物";

    /*
     * 请求次数
     */
    private static int linshi = 0;
    private static int latiao = 0;
    private static int song = 0;
    private static int gold = 0;
    private static int white_gold = 0;
    private static int cold_clean = 0;
    private static int diamond = 0;
    private static int liwu = 0;


    // 根据消息类型 构造返回消息
    public static String buildXml(Map<String, String> map) {
        String result;
        String msgType = map.get("MsgType").toString();
        System.out.println("MsgType:" + msgType);
        if (msgType.toUpperCase().equals("TEXT")) {

            /*
             * 根据内容的不同回复不同的内容
             */
            String content = map.get("Content");
            switch (content) {
                case LINSHI:
                    if (linshi == 0) {
                        result = buildTextMessage(map, "兑换成功，想要什么零食，可以回复具体的要求哦~~/::*/::*");
//						sendSMS(LINSHI, "零食即将送达~~",PURPLE_TEL_NUMBER);
//						sendSMS(LINSHI, "零食特权券兑换了！！！",SUZI_TEL_NUMBER);
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    linshi++;
                    break;
                case LATIAO:
                    if (latiao == 0) {
                        result = buildTextMessage(map, "辣条兑换成功，可以回复具体的要求~~/::*");
//						sendSMS(LATIAO, "辣条即将送达~~",PURPLE_TEL_NUMBER);
//						sendSMS(LATIAO, "辣条特权券兑换了！！！",SUZI_TEL_NUMBER);
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    latiao++;
                    break;
                case SONG:
                    if (song == 0) {
                        result = buildTextMessage(map, "点击下面链接：\nhttp://www.jiangsaixian.cn \n/::*/::*");
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    song++;
                    break;
                case GOLD:
                    if (gold == 0) {
                        result = buildTextMessage(map, "【黄金特权券】兑换成功，可以实现一个小小的要求或行使其他特权，请回复要行使的特权的内容~~/::*/::*");
//						sendSMS(GOLD, "请尽快行使特权~~",PURPLE_TEL_NUMBER);
//						sendSMS(GOLD, "黄金特权券！！！",SUZI_TEL_NUMBER);
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    gold++;
                    break;
                case WHITE_GOLD:
                    if (white_gold == 0) {
                        result = buildTextMessage(map, "【铂金特权券】兑换成功，可以实现一个大一点的要求或行使其他特权，请回复要行使的特权的内容~~/::*/::*");
//						sendSMS(WHITE_GOLD, "请尽快行使特权~~",PURPLE_TEL_NUMBER);
//						sendSMS(WHITE_GOLD, "铂金特权券！！！",SUZI_TEL_NUMBER);
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    white_gold++;
                    break;
                case COLD_CLEAN:
                    if (cold_clean == 0) {
                        result = buildTextMessage(map, "兑换成功，/:li马上消除一切矛盾和不愉快！！/::*/::*/:heart/:heart");
//						sendSMS(GOLD, "马上消除一切矛盾和不愉快！！",PURPLE_TEL_NUMBER);
//						sendSMS(GOLD, "矛盾消除券！！！",SUZI_TEL_NUMBER);
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    gold++;
                    break;
                case DIAMOND:
                    if (diamond == 0) {
                        result = buildTextMessage(map, "【钻石特权券】兑换成功！！可以实现任何要求，任何要求！\n请回复要行使的特权的内容~~");
//						sendSMS(DIAMOND, "请尽快行使特权~~",PURPLE_TEL_NUMBER);
//						sendSMS(DIAMOND, "钻石特权券！！！",SUZI_TEL_NUMBER);
                    } else {
                        result = buildTextMessage(map, "已经兑换了哦~~/:pig/:pig");
                    }
                    diamond++;
                    break;

                case LIWU:
                    if (liwu <= 10) {
                        result = buildTextMessage(map, "直接回复兑换券或特权券号码，兑换礼物~~/::*/::*\n（ps1：其中特权券用于行使一次特权，可以是实现一个要求，或者其他）\n（ps2：一经兑换，就此作废，请慎用）");
                    } else {
                        result = buildTextMessage(map, ":pig");
                    }
                    liwu++;
                    break;

                case "我爱你":
                    result = buildTextMessage(map, "我也爱你");
                    break;

                case "傻逼":
                    result = buildTextMessage(map, "哼哼");
                    break;
                default:
                    if (content.contains("傻") || content.contains("混蛋") || content.contains("哼") || content.contains("蠢")) {
                        result = buildTextMessage(map, "哼哼");
                    } else {
                        result = "success";
                    }
                    break;
            }


        } else if (msgType.toUpperCase().equals("EVENT")) {
            // 如果是被关注就回复欢迎消息
            result = buildTextMessage(map, "欢迎关注~~/:rose/:rose");
        } else {
            // 如果是其他类型的就不回复。设置为空字符串
            result = buildTextMessage(map, "/:rose");
        }
        return result;
    }

    /**
     * 构造文本消息
     *
     * @param map
     * @param content
     * @return
     */
    private static String buildTextMessage(Map<String, String> map,
                                           String content) {
        // 发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 文本消息XML数据格式 <xml> <ToUserName><![CDATA[toUser]]></ToUserName>
         * <FromUserName><![CDATA[fromUser]]></FromUserName>
         * <CreateTime>1348831860</CreateTime>
         * <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[this is a
         * test]]></Content> <MsgId>1234567890123456</MsgId> </xml>
         */
        return String.format("<xml>"
                        + "<ToUserName><![CDATA[%s]]></ToUserName>"
                        + "<FromUserName><![CDATA[%s]]></FromUserName>"
                        + "<CreateTime>%s</CreateTime>"
                        + "<MsgType><![CDATA[text]]></MsgType>"
                        + "<Content><![CDATA[%s]]></Content>" + "</xml>", fromUserName,
                toUserName, getUtcTime(), content);
    }

    private static String getUtcTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {
        }
        return String.valueOf(dd);
    }
    /**
     * 发送短信
     *
     * @param code
     * @param content
     */
//	private static void sendSMS(String code, String content,String tel)
//	{
//		try
//		{
//			// 设置超时时间-可自行调整
//			System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//			System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//			// 初始化ascClient需要的几个参数
//			final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
//			final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）
//			// 替换成你的AK
//			final String accessKeyId = "XXXXX";// 你的accessKeyId,参考本文档步骤2
//			final String accessKeySecret = "XXXXXXX";// 你的accessKeySecret，参考本文档步骤2
//			// 初始化ascClient,暂时不支持多region（请勿修改）
//			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
//					accessKeyId, accessKeySecret);
//			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
//					domain);
//			IAcsClient acsClient = new DefaultAcsClient(profile);
//			// 组装请求对象
//			SendSmsRequest request = new SendSmsRequest();
//			// 使用post提交
//			request.setMethod(MethodType.POST);
//			// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
//			request.setPhoneNumbers(tel);
//			// 必填:短信签名-可在短信控制台中找到
//			request.setSignName("筱屋枫林");
//			// 必填:短信模板-可在短信控制台中找到
//			request.setTemplateCode("SMS_11XXXX4");
//			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
//			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
//			String SMSContentString = "{\"code\":\"" + code + "\", \"content\":\"" +content+ "\"}";
//			request.setTemplateParam(SMSContentString);
//			// 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
//			// request.setSmsUpExtendCode("90997");
//			// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//			request.setOutId("yourOutId");
//			// 请求失败这里会抛ClientException异常
//			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
//			if (sendSmsResponse.getCode() != null
//					&& sendSmsResponse.getCode().equals("OK"))
//			{
//				System.out.println("短信发送成功！！！");
//			}
//		} catch (Exception e)
//		{
//
//		}
//	}

}
