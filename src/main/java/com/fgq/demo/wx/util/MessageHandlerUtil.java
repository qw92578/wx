package com.fgq.demo.wx.util;

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
    /**
     * 微信能接受的7中消息类型
     */
    // 请求消息类型：文本
    public static final String REQ_MESSAGE_TYPE_TEXT = "TEXT";
    // 请求消息类型：图片
    public static final String REQ_MESSAGE_TYPE_IMAGE = "IMAGE";
    // 请求消息类型：语音
    public static final String REQ_MESSAGE_TYPE_VOICE = "VOICE";
    // 请求消息类型：视频
    public static final String REQ_MESSAGE_TYPE_VIDEO = "VIDEO";
    // 请求消息类型：地理位置
    public static final String REQ_MESSAGE_TYPE_LOCATION = "LOCATION";
    // 请求消息类型：链接
    public static final String REQ_MESSAGE_TYPE_LINK = "LINK";
    //小视频
    private static final String REQ_MESSAGE_TYPE_SHORTVIDEO = "SHORTVIDEO";


    // 事件推送
    public static final String REQ_MESSAGE_TYPE_EVENT = "EVENT";
    // 事件类型：subscribe(订阅)
    public static final String EVENT_TYPE_SUBSCRIBE = "SUBSCRIBE";
    // 事件类型：unsubscribe(取消订阅)
    public static final String EVENT_TYPE_UNSUBSCRIBE = "UNSUBSCRIBE";


    /**
     * 事件类型：scan(用户已关注时的扫描带参数二维码)
     */
    public static final String EVENT_TYPE_SCAN = "SCAN";

    // 事件类型：LOCATION(上报地理位置)
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
    // 事件类型：CLICK(自定义菜单)
    public static final String EVENT_TYPE_CLICK = "CLICK";
    // View
    public static final String EVENT_TYPE_VIEW = "VIEW";


    /**
     * 消息回复类型 5种
     * 文本、图片、图文、语音、视频、音乐
     */
    // 响应消息类型：文本
    public static final String RESP_MESSAGE_TYPE_TEXT = "TEXT";
    // 响应消息类型：图片
    public static final String RESP_MESSAGE_TYPE_IMAGE = "IMAGE";
    // 响应消息类型：语音
    public static final String RESP_MESSAGE_TYPE_VOICE = "VOICE";
    // 响应消息类型：视频
    public static final String RESP_MESSAGE_TYPE_VIDEO = "VIDEO";
    // 响应消息类型：音乐
    public static final String RESP_MESSAGE_TYPE_MUSIC = "MUSIC";
    // 响应消息类型：图文
    public static final String RESP_MESSAGE_TYPE_NEWS = "NEWS";


    // 响应消息类型：消息转发到多客服
    public static final String RESP_MESSAGE_TYPE_TRANSFER_CUSTOMER_SERVICE = "TRANSFER_CUSTOMER_SERVICE";


    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return Map<String,String>
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>(16);

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


    /*
     * 电话号码
     */
    private static final String PURPLE_TEL_NUMBER = "xxxx";
    private static final String SUZI_TEL_NUMBER = "xxxxx";

    //发送特定的文本消息的回答
    private static final String LINSHI = "13162031761";
    private static final String LATIAO = "13162031762";
    private static final String SONG = "13162031763";
    private static final String GOLD = "13162031764";
    private static final String WHITE_GOLD = "13162031765";
    private static final String COLD_CLEAN = "13162031766";
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


    /**
     *  根据消息类型 构造返回消息text
     *
     *  微信接收类型 7中
     *
     *  　1 文本消息
     * 　　2 图片消息
     * 　　3 语音消息
     * 　　4 视频消息
     * 　　5 小视频消息
     * 　　6 地理位置消息
     * 　　7 链接消息
     */
    public static String buildXml(Map<String, String> map) {
        String result = null;
        String msgType = map.get("MsgType").toUpperCase();
        String mediaId = map.get("mediaId");
        if(msgType.equals(REQ_MESSAGE_TYPE_TEXT)) {
            //文本
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
                        result = buildTextMessage(map, "点击下面链接：\nhttp://www.guangqiang.gq \n/::*/::*");
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
                    result = buildTextMessage(map, "你不配");
                    break;
                case "音乐":
                    Music music = new Music();
                    music.setTitle("那女孩对我说");
                    music.setDescription("wx/music");
                    music.setMusicUrl("http://5yswbx.natappfree.cc/wx/test.mp3");
                    music.setHQMusicUrl("http://5yswbx.natappfree.cc/wx/test.mp3");
                    result = buildMusicMessage(map,music);
                    break;
                default:
                    if (content.contains("傻") || content.contains("混蛋") || content.contains("哼") || content.contains("蠢")
                            || content.contains("巴")) {
                        result = buildTextMessage(map, "你是傻逼！没的救");
                    } else {
                       result = buildTextMessage(map, "我听不懂你再说什么");
                    }
                    break;
            }
        }else if(msgType.equals(REQ_MESSAGE_TYPE_IMAGE)) {
            //图片
            result = buildImageMessage(map,mediaId);
        }else if(msgType.equals(REQ_MESSAGE_TYPE_VOICE)) {
            //语言
        }else if(msgType.equals(REQ_MESSAGE_TYPE_VIDEO)) {
            //视频
//            result = buildVideoMessage(map);
        }else if(msgType.equals(REQ_MESSAGE_TYPE_LOCATION)) {
            //位置
//            result = buildLocationMessage(map);
        }else if(msgType.equals(REQ_MESSAGE_TYPE_LINK)) {
            //链接
//            result = buildLinkMessage(map);
        }else if(msgType.equals(REQ_MESSAGE_TYPE_SHORTVIDEO)) {
            //小视频
//            result = buildShortvideoMessage(map);
        }else if(msgType.equals(REQ_MESSAGE_TYPE_EVENT)) {
            //处理事件消息,用户在关注与取消关注公众号时，微信会向我们的公众号服务器发送事件消息,
            // 开发者接收到事件消息后就可以给用户下发欢迎消息
            result = buildTextMessage(map, "你是因为我帅才关注我的吗？~~/:rose/:rose");
        }else {
            // 如果是其他类型的就不回复。设置为空字符串
            result =  buildTextMessage(map, "/:rose");
        }
        return result;
    }

    /**
     * 构造图片消息
     * @param map 封装了解析结果的Map
     * @param mediaId 通过素材管理接口上传多媒体文件得到的id
     * @return 图片消息XML字符串
     */
    private static String buildImageMessage(Map<String, String> map, String mediaId) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 图片消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[image]]></MsgType>
         <Image>
         <MediaId><![CDATA[media_id]]></MediaId>
         </Image>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[image]]></MsgType>" +
                        "<Image>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Image>" +
                        "</xml>",
                fromUserName, toUserName, getUtcTime(), mediaId);
    }

    /**
     * 构造音乐消息
     * @param map 封装了解析结果的Map
     * @param music 封装好的音乐消息内容
     * @return 音乐消息XML字符串
     */
    private static String buildMusicMessage(Map<String, String> map, Music music) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 音乐消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[music]]></MsgType>
         <Music>
         <Title><![CDATA[TITLE]]></Title>
         <Description><![CDATA[DESCRIPTION]]></Description>
         <MusicUrl><![CDATA[MUSIC_Url]]></MusicUrl>
         <HQMusicUrl><![CDATA[HQ_MUSIC_Url]]></HQMusicUrl>
         <ThumbMediaId><![CDATA[media_id]]></ThumbMediaId>
         </Music>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[music]]></MsgType>" +
                        "<Music>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "   <MusicUrl><![CDATA[%s]]></MusicUrl>" +
                        "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>" +
                        "</Music>" +
                        "</xml>",
                fromUserName, toUserName, getUtcTime(), music.getTitle(), music.getDescription(), music.getMusicUrl(), music.getHQMusicUrl());
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

    /**
     * 构造文本消息
     *
     * @param map
     * @param content
     * @return
     */
    private static String buildTextMessage(Map<String, String> map, String content) {
        // 发送方帐号  oxew2twxFLPVVlyF11YvE9F9hxGI  好大的风啊
        String fromUserName = map.get("FromUserName");
        // 开发者微信号   gh_ee458f77825e
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

    /**
     * 获取当前时间
     * @return
     */
    private static String getUtcTime() {
        // 如果不需要格式,可直接用dt,dt就是当前系统时间
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {
        }
        return String.valueOf(dd);
    }

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

}
