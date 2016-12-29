package com.deleidos.analytics.websocket.event;

import java.util.ArrayList;

/**
 * This event is fired when a new consumer is added.
 * 
 * @author klein
 */
public class NewConsumerEvent {

	private String id;
	private String topic;

	/**
	 * Constructor.
	 * 
	 * @param id
	 */
	public NewConsumerEvent(String id, String topic) {
		this.id = id;
		this.topic = topic;
	}

	public String getId() {
		return id;
	}
	
	public String getTopic() {
		return topic;
	}

}
