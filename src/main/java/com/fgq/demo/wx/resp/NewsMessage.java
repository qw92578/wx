package com.fgq.demo.wx.resp;

import lombok.Data;

import java.util.List;

@Data
public class NewsMessage extends BaseMessage {

	private int ArticleCount;

	private List<Article> Articles;

}
