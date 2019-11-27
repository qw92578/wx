package com.fgq.demo.wx.resp;

import lombok.Data;

@Data
public class BaseMessage {

    private String ToUserName;

    private String FromUserName;

    private long CreateTime;

    private String MsgType;


}
