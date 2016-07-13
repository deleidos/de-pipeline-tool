package com.deleidos.analytics.pubsub.query;

import com.deleidos.analytics.common.datetime.TimeWindow;

public class PubSubResult {

	private String message;
	private String sessionId;
	private String topic;
	private TimeWindow window;
	
	public PubSubResult(String sessionId, String topic, TimeWindow window) {
		super();
		this.sessionId = sessionId;
		this.topic = topic;
		this.window = window;
		this.message = "DEFAULT";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public TimeWindow getWindow() {
		return window;
	}

	public void setWindow(TimeWindow window) {
		this.window = window;
	}
	
}
