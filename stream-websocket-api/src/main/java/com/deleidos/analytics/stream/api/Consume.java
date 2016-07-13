package com.deleidos.analytics.stream.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.stream.StreamManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the stream endpoint, this message sets a sending WebSocket endpoint as a live stream consumer
 * of data pushed to the given topic (consume variable).
 */
public class Consume extends BaseWebSocketMessage {

	public String consume;
	
	public Consume() {
	}

	public Consume(String value) {
		this.consume = value;
	}

	public String getConsume() {
		return consume;
	}
	public void setConsume(String consume) {
		this.consume = consume;
	}

	@Override
	@Path("/consume")
	@GET
	public void processMessage() {
		StreamManager.getInstance().addConsumer(webSocketId, consume);
	}
}
