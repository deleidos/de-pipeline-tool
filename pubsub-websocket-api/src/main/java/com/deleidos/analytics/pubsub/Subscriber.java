package com.deleidos.analytics.pubsub;

import com.deleidos.analytics.common.datetime.TimeWindow;

public class Subscriber {

	private String webSocketId;
	private String topic;
	private TimeWindow window;
	private String sessionId;
	
	public Subscriber(String webSocketId, String topic, TimeWindow window, String sessionId) {
		super();
		this.webSocketId = webSocketId;
		this.topic = topic;
		this.window = window;
		this.sessionId = sessionId;
	}
	
	public String getWebSocketId() {
		return webSocketId;
	}
	public void setWebSocketId(String webSocketId) {
		this.webSocketId = webSocketId;
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
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		result = prime * result + ((webSocketId == null) ? 0 : webSocketId.hashCode());
		result = prime * result + ((window == null) ? 0 : window.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subscriber other = (Subscriber) obj;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		if (webSocketId == null) {
			if (other.webSocketId != null)
				return false;
		} else if (!webSocketId.equals(other.webSocketId))
			return false;
		if (window != other.window)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Subscriber [webSocketId=" + webSocketId + ", topic=" + topic + ", window=" + window + ", sessionId="
				+ sessionId + "]";
	}
	
}
