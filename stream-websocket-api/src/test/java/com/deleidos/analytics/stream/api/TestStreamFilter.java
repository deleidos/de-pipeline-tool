package com.deleidos.analytics.stream.api;

import com.deleidos.analytics.stream.StreamFilter;

public class TestStreamFilter implements StreamFilter {

	@Override
	public String getTopic() {
		return "stream_topic_1";
	}

	@Override
	public String filterMessage(String message) {
		if (message.contains("test")) {
			return null;
		} else {
			return message;
		}
	}

}
