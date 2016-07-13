package com.deleidos.analytics.stream.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.stream.StreamManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the stream endpoint, this message sets a sending WebSocket endpoint as a live stream producer
 * of data for the given topic (stream variable).
 */
public class Stream extends BaseWebSocketMessage {
	
	public String stream;
	
	public Stream() {
	}

	public Stream(String value) {
		this.stream = value;
	}
	
	public String getStream() {
		return stream;
	}
	public void setStream(String stream) {
		this.stream = stream;
	}

	@Override
	@Path("/stream")
	@GET
	public void processMessage() {
		StreamManager.getInstance().addProducer(webSocketId, stream);
	}
}
