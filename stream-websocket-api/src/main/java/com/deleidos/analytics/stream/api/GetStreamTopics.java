package com.deleidos.analytics.stream.api;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.stream.StreamManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the req-resp endpoint, this message retrieves the current list of Stream Topics.
 */
public class GetStreamTopics extends BaseWebSocketMessage {

	public String request;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getStreamTopics")
	@GET
	public void processMessage() throws Exception {
		Set<String> topics = StreamManager.getInstance().getTopics();
		sendResponse(new TopicsWrapper(topics));
	}
}
