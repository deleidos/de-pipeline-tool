package com.deleidos.analytics.stream.api;

public class TestConsumeMessage {

	private String test;
	private String topic;
	
	public TestConsumeMessage() {
		super();
	}

	public TestConsumeMessage(String test, String topic) {
		super();
		this.test = test;
		this.topic = topic;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
