package com.deleidos.analytics.pubsub;

import java.util.ArrayList;
import java.util.List;

import com.deleidos.analytics.common.datetime.TimeWindow;

public class Publisher {

	private String webSocketId;
	private String timeStamp;
	private String content;
	private TimeWindow window;
	private List<String> topics;
	
	public Publisher(String webSocketId) {
		this.webSocketId = webSocketId;
		topics = new ArrayList<String>();
	}
	
	public String getWebSocketId() {
		return webSocketId;
	}
	public void setWebSocketId(String webSocketId) {
		this.webSocketId = webSocketId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	public TimeWindow getWindow() {
		return window;
	}
	public void setWindow(TimeWindow window) {
		this.window = window;
	}
}
