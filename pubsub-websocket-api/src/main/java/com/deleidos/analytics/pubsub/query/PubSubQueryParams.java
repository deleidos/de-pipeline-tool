package com.deleidos.analytics.pubsub.query;

import java.util.Date;

import com.deleidos.analytics.common.datetime.TimeWindow;

public class PubSubQueryParams {

	private String topic;
	private String content;
	private Date start;
	private Date end;
	private TimeWindow window;
	private String sessionId;

	public PubSubQueryParams(String topic, String content, Date start, Date end, TimeWindow window, String sessionId) {
		super();
		this.topic = topic;
		this.content = content;
		this.start = start;
		this.end = end;
		this.window = window;
		this.sessionId = sessionId;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public TimeWindow getWindow() {
		return window;
	}
	public void setWindow(TimeWindow window) {
		this.window = window;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
